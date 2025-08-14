package com.example.loginservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class SignInRequest  implements Serializable {
    @NotBlank(message = "Username cannot be empty")
    @Email
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    private String verificationCode;
}
