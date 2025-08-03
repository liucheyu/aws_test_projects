package com.example.loginservice.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ACCESS_DENIED(401, "Access Denied"),
    SUCCESS(200, "Success");
    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
