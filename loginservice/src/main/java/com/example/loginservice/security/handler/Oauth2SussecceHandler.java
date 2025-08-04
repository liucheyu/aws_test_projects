package com.example.loginservice.security.handler;

import com.example.loginservice.common.ApiResponse;
import com.example.loginservice.model.CustomOAuth2User;
import com.example.loginservice.model.User;
import com.example.loginservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2SussecceHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 authentication successful");
        log.info(authentication.toString());
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser(); // 獲取您自己的 User 實體
        String jwt = jwtUtil.generateToken(user);

        response.setContentType("application/json");

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(jwt)));
    }
}
