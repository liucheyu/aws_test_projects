package com.example.loginservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank(message = "Username cannot be empty")
    @Email
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    private String verificationCode;
}
