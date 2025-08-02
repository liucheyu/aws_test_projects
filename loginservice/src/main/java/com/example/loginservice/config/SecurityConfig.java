package com.example.loginservice.config;

import com.example.loginservice.security.filter.UnifiedLoginFilter;
import com.example.loginservice.model.CustomOAuth2User;
import com.example.loginservice.model.User;
import com.example.loginservice.security.provider.SmsAuthenticationProvider;
import com.example.loginservice.service.CustomUserDetailsService;
import com.example.loginservice.service.JwtService;
import com.example.loginservice.service.SmsService;
import com.example.loginservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final SmsAuthenticationProvider smsAuthenticationProvider;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    //private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 啟用 CORS
                .cors(AbstractHttpConfigurer::disable) // 實際生產環境中需要配置 CORS
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 配置請求授權
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/unified-login", "/oauth2/**", "/login/**").permitAll() // 移除 /api/auth/send-otp
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // 其他所有請求都需要認證
                )
                // 配置會話管理為無狀態 (JWT 認證不需要會話)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置認證提供者
                .authenticationProvider(authenticationProvider())
                .authenticationProvider(smsAuthenticationProvider)
                // 將自定義的 UnifiedLoginFilter 放在 UsernamePasswordAuthenticationFilter 之前
                // UnifiedLoginFilter 構造函數注入 smsService
                .addFilterBefore(getUnifiedLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                // 添加 JWT 過濾器在 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtService, UsernamePasswordAuthenticationFilter.class)
                // 配置 OAuth 2.0 登入
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz.baseUri("/oauth2/authorization")) // 授權端點
                        .redirectionEndpoint(redirect -> redirect.baseUri("/oauth2/callback/*")) // 回調端點
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService())) // 用於獲取使用者資訊
                        .successHandler((request, response, authentication) -> {
                            // OAuth2 登入成功處理
                            // 在這裡生成 JWT 並返回給前端
//                            String username = authentication.getName(); // 可能是 Google ID 或 Facebook ID
//                            String jwt = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(username)); // 重新載入使用者以獲取完整UserDetails
                            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                            User user = customOAuth2User.getUser(); // 獲取您自己的 User 實體
                            String jwt = jwtUtil.generateToken(user);

                            response.setContentType("application/json");
                            response.getWriter().write("{\"token\":\"" + jwt + "\", \"message\":\"OAuth2 login successful\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            // OAuth2 登入失敗處理
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("OAuth2 login failed: " + exception.getMessage());
                        })

                )
                // 配置登出
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // 登出 URL
                        .addLogoutHandler(logoutHandler()) // 清除 JWT (後續實現)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout successful");
                        })
                );

        return http.build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // 認證提供者 (用於帳號密碼認證)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // 認證管理器 (用於觸發認證過程)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 自定義 OAuth2UserService 處理第三方使用者資訊
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, facebook
            String providerId = oauth2User.getName(); // OAuth2 User 的 ID

            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String userPictureUrl = oauth2User.getAttribute("picture");

            // 根據 provider 和 providerId 在您的系統中查找或創建使用者
            // 並返回一個實現 UserDetails 的物件 (這裡使用我們自己的 User 類)
            UserDetails userDetails = customUserDetailsService.loadOrCreateOAuth2User(registrationId, providerId, email, name, userPictureUrl);


            return new CustomOAuth2User((User) userDetails, oauth2User);
        };
    }

    // 登出處理器 (清除 JWT 通常由前端刪除，這裡只是示例)
    @Bean
    public LogoutHandler logoutHandler() {
        return new SecurityContextLogoutHandler(); // 可以實現更複雜的 JWT 黑名單邏輯
    }

    @Bean
    public UnifiedLoginFilter getUnifiedLoginFilter() {
        return new UnifiedLoginFilter();
    }

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        // 通常 ClientRegistrationRepository 會由 Spring Boot 自動配置，
//        // 根據 application.properties/yml 中的 spring.security.oauth2.client.registration.*
//        // 這裡只是為了確保您可以注入它
//        return clientRegistrationRepository; // 實際注入進來的
//    }


}