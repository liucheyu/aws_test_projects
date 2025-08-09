package com.example.loginservice.common;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class ValidateException extends AuthenticationException {
    private final ResponseCode responseCode;

    public ValidateException(ResponseCode code) {
        super(code.getMessage());
        this.responseCode = code;
    }

    public ValidateException(ResponseCode code, String customMessage) {
        super(customMessage);
        this.responseCode = code;
    }
}
