package com.example.loginservice.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    USER_ALREADY_EXISTS(1001, "User already exists"),
    PHONE_NUMBER_ALREADY_USED(1002, "Phone number already used"),
    DATA_ALREADY_EXISTS(1003, "Data already exists"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ACCESS_DENIED(401, "Access Denied"),
    BAD_CREDENTIALS(401, "Bad Credentials"),
    BAD_REQUEST(400, "Bad Request"),
    SUCCESS(200, "Success");

    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
