package com.example.loginservice.controller;

import com.example.loginservice.common.ApiCommonException;
import com.example.loginservice.common.ApiResponse;
import com.example.loginservice.common.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 專門處理自定義 BusinessException 的方法
     */
    @ExceptionHandler(ApiCommonException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(ApiCommonException ex) {
        // 從 BusinessException 中獲取 ErrorCode
        ResponseCode errorCode = ex.getResponseCode();

        // 使用 ErrorCode 和 Exception 的訊息來構建 ApiResponse
        return ResponseEntity
                .ok()
                .body(ApiResponse.error(errorCode, ex.getMessage()));
    }

    // 處理所有未處理的通用異常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {

        return ResponseEntity
                .status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 從異常中提取所有欄位的錯誤訊息
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // 返回一個統一的錯誤響應格式
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "Validation failed: " + errorMessage));
    }

    // 您可以保留其他特定的異常處理方法
    // 例如：
    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
    //     return ResponseEntity
    //             .status(ErrorCode.BAD_REQUEST.getStatus())
    //             .body(ApiResponse.error(ErrorCode.BAD_REQUEST, ex.getMessage()));
    // }
}