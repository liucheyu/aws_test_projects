package com.example.loginservice.security.auth;


import com.example.loginservice.model.UnifiedLoginRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定義的 AuthenticationToken，用於電話號碼 + OTP 認證。
 */
public class OptAuthenticationToken extends AbstractAuthenticationToken {

    private UnifiedLoginRequest request;
    private String otp;

    public OptAuthenticationToken(UnifiedLoginRequest request, String otp) {
        super(null);
        this.request = request;
        this.otp = otp;
    }

    public UnifiedLoginRequest getRequest() {
        return request;
    }

    public String getOtp() {
        return otp;
    }

    @Override
    public Object getCredentials() {
        return this.otp;
    }

    @Override
    public Object getPrincipal() {
        return switch (request.getType()) {
            case 1 -> request.getUsername();
            case 2 -> request.getMobilePhoneNumber();
            default -> throw new IllegalStateException("Unexpected value: " + request.getType());
        };
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.otp = null; // 清除敏感信息
    }
}