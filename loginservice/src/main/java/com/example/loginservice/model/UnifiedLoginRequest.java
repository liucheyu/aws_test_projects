package com.example.loginservice.model;

import lombok.Data;

@Data
public class UnifiedLoginRequest {
    private String username;    // 用於帳號密碼登入
    private String password;    // 用於帳號密碼登入
    private String mobilePhoneNumber; // 用於電話號碼登入 (發送 OTP 或驗證 OTP 都用此欄位)
}
