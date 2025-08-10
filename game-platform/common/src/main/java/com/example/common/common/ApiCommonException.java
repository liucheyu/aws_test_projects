package com.example.common.common;

import lombok.Getter;

@Getter
public class ApiCommonException extends RuntimeException {
    private final ResponseCode responseCode;

    public ApiCommonException(ResponseCode code) {
        super(code.getMessage());
        this.responseCode = code;
    }

    public ApiCommonException(ResponseCode code, String customMessage) {
        super(customMessage);
        this.responseCode = code;
    }
}
