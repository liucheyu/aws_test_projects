package com.example.loginservice.common;

import lombok.Getter;

@Getter
public enum ResponseCode {

    USER_ALREADY_EXISTS(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    PHONE_NUMBER_ALREADY_USED(1003, "Phone number already used"),
    DATA_ALREADY_EXISTS(1004, "Data already exists"),
    DATA_NOT_FOUND(1005, "Data not found"),
    INVALID_ACTIVATION_CODE(1006, "Invalid activation code"),

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
