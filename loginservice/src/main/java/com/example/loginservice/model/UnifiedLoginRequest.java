package com.example.loginservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnifiedLoginRequest {
    @NotEmpty
    private String username;    // 用於帳號密碼登入
    @NotEmpty
    private String password;    // 用於帳號密碼登入
    @Size(min = 1, max = 2)
    private int type; // 1:email, 2:mobile phone
    //private String mobilePhoneNumber; // 用於電話號碼登入 (發送 OTP 或驗證 OTP 都用此欄位)
}
