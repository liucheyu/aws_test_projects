package com.example.common.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 共通 API 響應格式
 *
 * @param <T> data 欄位的泛型類型
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {

    // 響應狀態碼，例如 200, 400, 401 等
    private int status;

    // 響應訊息，提供給前端的文字描述
    private String message;

    // 實際的業務資料，使用泛型使其適用於任何資料類型
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 成功響應的靜態工廠方法
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.SUCCESS.getStatus(), ResponseCode.SUCCESS.getMessage(), null);
    }
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getStatus(), ResponseCode.SUCCESS.getMessage(), data);
    }

    // 覆載成功響應方法，允許自定義訊息
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getStatus(), message, data);
    }

    // 覆載成功響應方法，允許自定義訊息
    public static <T> ApiResponse<T> success(ResponseCode responseCode, String message, T data) {
        return new ApiResponse<>(responseCode.getStatus(), message, data);
    }

    // 失敗響應的靜態工廠方法，直接使用 ErrorCode
    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode.getStatus(), responseCode.getMessage(), null);
    }

    // 覆載失敗響應方法，允許自定義訊息
    public static <T> ApiResponse<T> error(ResponseCode responseCode, String customMessage) {
        return new ApiResponse<>(responseCode.getStatus(), customMessage, null);
    }
}