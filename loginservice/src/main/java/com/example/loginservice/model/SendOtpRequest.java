package com.example.loginservice.model;

import lombok.Data;

@Data
public class SendOtpRequest {
    private String phoneNumber;
}