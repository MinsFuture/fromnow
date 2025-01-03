package com.knu.fromnow.api.global.spec.api;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiDataResponse<T> {

    private boolean status;
    private int code;
    private String message;
    private T data;

    @Builder
    public ApiDataResponse(boolean status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> ApiDataResponse<T> successResponse(String message, T data) {
        return ApiDataResponse.<T>builder()
                .status(true)
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

}
