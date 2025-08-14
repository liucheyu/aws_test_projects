package com.example.loginservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignInMobilePhoneRequest extends SignInRequest {
    @NotEmpty
    @Min(3)
    private String mobilePhoneNumber;
    @NotEmpty
    private String countryCode;

}
