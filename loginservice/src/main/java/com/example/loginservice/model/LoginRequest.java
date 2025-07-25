package com.example.loginservice.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}