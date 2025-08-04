package com.example.loginservice.security.filter;

import com.example.loginservice.common.ApiResponse;
import com.example.loginservice.model.UnifiedLoginRequest;
import com.example.loginservice.security.auth.SmsAuthenticationToken;
import com.example.loginservice.service.SmsService; // 引入 SmsService
import com.example.loginservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus; // 引入 HttpStatus
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UnifiedLoginFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SmsService smsService; // 新增注入 SmsService

    private static final String LOGIN_URL = "/api/unified-login";
    private static final String OTP_HEADER_NAME = "X-OTP"; // 定義 OTP Header 名稱

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 只處理指定登入 URL 的 POST 請求
        if (!request.getRequestURI().equals(LOGIN_URL) || !request.getMethod().equals(HttpMethod.POST.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 解析請求體為 UnifiedLoginRequest
            UnifiedLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), UnifiedLoginRequest.class);
            String otpHeader = request.getHeader(OTP_HEADER_NAME); // 獲取 OTP Header

            if (loginRequest.getMobilePhoneNumber() != null && loginRequest.getUsername() == null && loginRequest.getPassword() == null) {
                // 這是電話號碼相關的請求
                if (otpHeader == null || otpHeader.isEmpty()) {
                    // Scenario 1: 沒有 X-OTP Header -> 發送 OTP
                    smsService.generateOtp(loginRequest.getMobilePhoneNumber());
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", "OTP sent successfully. Please provide OTP in X-OTP header for login.")));
                    return; // 處理完畢，不繼續過濾器鏈
                } else {
                    // Scenario 2: 有 X-OTP Header -> 電話號碼 + OTP 登入驗證
                    Authentication authenticationRequest = new SmsAuthenticationToken(
                            loginRequest.getMobilePhoneNumber(),
                            otpHeader // OTP 從 Header 獲取
                    );
                    Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
                    handleSuccess(request, response, authenticationResult);
                }
            } else if (loginRequest.getUsername() != null && loginRequest.getPassword() != null && loginRequest.getMobilePhoneNumber() == null) {
                // Scenario 3: 帳號密碼登入
                Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );
                Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
                handleSuccess(request, response, authenticationResult);
            } else {
                // 其他無效的請求組合
                throw new BadCredentialsException("Invalid login request: Please provide either username/password or phone number (with/without OTP header).");
            }
// 驗證失敗應該會進入AuthenticationEntryPoint
//        } catch (AuthenticationException e) {
//            handleFailure(request, response, e);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", "Invalid request body or an internal server error occurred.")));
        }
    }

    private void handleSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(Map.of("token", jwt))));
    }

    private void handleFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", exception.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }


}