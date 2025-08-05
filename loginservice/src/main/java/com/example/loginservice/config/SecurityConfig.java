package com.example.loginservice.config;

import com.example.loginservice.security.filter.UnifiedLoginFilter;
import com.example.loginservice.model.CustomOAuth2User;
import com.example.loginservice.model.User;
import com.example.loginservice.security.handler.CustomAccessDeniedHandler;
import com.example.loginservice.security.handler.CustomAuthenticationEntryPoint;
import com.example.loginservice.security.handler.Oauth2FailureHandler;
import com.example.loginservice.security.handler.Oauth2SussecceHandler;
import com.example.loginservice.security.provider.CustomUsernamePasswordAuthenticationProvider;
import com.example.loginservice.security.provider.SmsAuthenticationProvider;
import com.example.loginservice.service.CustomUserDetailsService;
import com.example.loginservice.service.JwtService;
import com.example.loginservice.service.SmsService;
import com.example.loginservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final SmsAuthenticationProvider smsAuthenticationProvider;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final Oauth2SussecceHandler oauth2SussecceHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;
    private final CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider;
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
                        .requestMatchers("/api/unified-login", "/oauth2/**", "/login/oauth2/**", "/alive", "/api/user/sign-in/**").permitAll() // 移除 /api/auth/send-otp
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // 其他所有請求都需要認證
                )
                // 配置會話管理為無狀態 (JWT 認證不需要會話)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exhdl -> exhdl
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // 配置認證提供者
                .authenticationProvider(customUsernamePasswordAuthenticationProvider)
                .authenticationProvider(smsAuthenticationProvider)
                // 將自定義的 UnifiedLoginFilter 放在 UsernamePasswordAuthenticationFilter 之前
                // UnifiedLoginFilter 構造函數注入 smsService
                .addFilterBefore(getUnifiedLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                // 添加 JWT 過濾器在 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtService, UsernamePasswordAuthenticationFilter.class)
                // 配置 OAuth 2.0 登入
                .oauth2Login(oauth2 -> oauth2
                                .authorizationEndpoint(authz -> authz.baseUri("/oauth2/authorization")) // 授權端點
//                        .redirectionEndpoint(redirect -> redirect.baseUri("/oauth2/callback/*")) // 回調端點
                                .userInfoEndpoint(userInfo -> {
                                    userInfo.oidcUserService(oidcUserService());
                                    userInfo.userService(oAuth2UserService());
                                }) // 用於獲取使用者資訊
                                .successHandler(oauth2SussecceHandler)
                                .failureHandler(oauth2FailureHandler)
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

            log.info("oauth2User User: {}", oauth2User);

            // 根據 provider 和 providerId 在您的系統中查找或創建使用者
            // 並返回一個實現 UserDetails 的物件 (這裡使用我們自己的 User 類)
            User user = customUserDetailsService.loadOrCreateOAuth2User(registrationId, providerId, email, name, userPictureUrl);

            return new CustomOAuth2User(user, oauth2User, null);
        };
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);

            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            // TODO
            // 1) Fetch the authority information from the protected resource using accessToken
            // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities

            // 3) Create a copy of oidcUser but use the mappedAuthorities instead
            ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
            String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
            if (StringUtils.hasText(userNameAttributeName)) {
                oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), userNameAttributeName);
            } else {
                oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }

            log.info("OIDC User: {}", oidcUser);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String providerId = oidcUser.getName();
            String email = oidcUser.getAttribute("email");
            String name = oidcUser.getAttribute("name");
            String userPictureUrl =  oidcUser.getAttribute("picture");

            User user = customUserDetailsService.loadOrCreateOAuth2User(registrationId, providerId, email, name, userPictureUrl);

            return new CustomOAuth2User(user, null, oidcUser);
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
//        // 根據 application.yml 中的 spring.security.oauth2.client.registration.*
//        // 這裡只是為了確保您可以注入它
//        return clientRegistrationRepository; // 實際注入進來的
//    }


}