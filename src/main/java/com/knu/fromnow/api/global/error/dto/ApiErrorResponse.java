package com.knu.fromnow.api.global.error.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiErrorResponse<T> {
    private boolean status;
    private int code;
    private String message;
    private T data;

    @Builder
    public ApiErrorResponse(boolean status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
