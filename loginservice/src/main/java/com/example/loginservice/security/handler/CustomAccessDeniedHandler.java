package com.example.loginservice.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 設定 HTTP 狀態碼為 403 (Forbidden)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 設定回應內容類型為 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 寫入 JSON 錯誤訊息
        response.getWriter().write("{\"message\": \"Access Denied: You do not have sufficient permissions to access this resource\"}");
    }
}