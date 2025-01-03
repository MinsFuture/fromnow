package com.knu.fromnow.api.global.spec.api;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiBasicResponse {
    private boolean status;
    private int code;
    private String message;

    @Builder
    public ApiBasicResponse(boolean status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ApiBasicResponse successResponse(String message){
        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message(message)
                .build();
    }
}
