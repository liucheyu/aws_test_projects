package com.example.loginservice.model;

import lombok.Data;

@Data
public class SmsLoginRequest {
    private String phoneNumber;
    private String otp;
}