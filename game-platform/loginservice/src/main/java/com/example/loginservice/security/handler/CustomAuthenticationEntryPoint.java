package com.example.loginservice.security.handler;

import com.example.common.common.ApiResponse;
import com.example.common.common.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 設定 HTTP 狀態碼為 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 設定回應內容類型為 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 寫入 JSON 錯誤訊息
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(ResponseCode.ACCESS_DENIED)));
    }
}