package com.example.loginservice.security.handler;

import com.example.loginservice.model.CustomOAuth2User;
import com.example.loginservice.model.User;
import com.example.loginservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SussecceHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 登入成功處理
        // 在這裡生成 JWT 並返回給前端
//                            String username = authentication.getName(); // 可能是 Google ID 或 Facebook ID
//                            String jwt = jwtUtil.generateToken(customUserDetailsService.loadUserByUsername(username)); // 重新載入使用者以獲取完整UserDetails
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser(); // 獲取您自己的 User 實體
        String jwt = jwtUtil.generateToken(user);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + jwt + "\", \"message\":\"OAuth2 login successful\"}");
    }
}
