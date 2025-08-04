package com.example.loginservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInEmailRequest {
    @NotBlank(message = "Username cannot be empty")
    @Email
    private String email;
    private String password;
}
