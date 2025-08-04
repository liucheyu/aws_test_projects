package com.example.loginservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInMobilePhoneRequest {
    @NotEmpty
    @Min(3)
    private long mobilePhoneNumber;
    @NotEmpty
    private String countryCode;
}
