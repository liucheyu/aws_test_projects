package com.example.loginservice.security.auth;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定義的 AuthenticationToken，用於電話號碼 + OTP 認證。
 */
public class ActivateCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // 通常是電話號碼
    private String otp; // 認證憑證 (OTP)
    private int type;// 1.email, 2sms

    // 用於未認證的構造函數 (當使用者提交電話號碼和 OTP 時)
    public ActivateCodeAuthenticationToken(String phoneNumber, String otp, int type) {
        super(null);
        this.principal = phoneNumber;
        this.otp = otp;
        this.type = type;
        setAuthenticated(false); // 標記為未認證
    }

    // 用於已認證的構造函數 (當 OTP 驗證成功後)
    public ActivateCodeAuthenticationToken(Object principal, String otp, int type, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.otp = otp;
        this.type = type;
        setAuthenticated(true); // 標記為已認證
    }

    @Override
    public Object getCredentials() {
        return this.otp;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }



    public void eraseCredentials() {
        super.eraseCredentials();
        this.otp = null; // 清除敏感信息
    }
}