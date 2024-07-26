package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiDiaryResponse<T> {

    private boolean status;
    private int code;
    private String message;
    private T data;

    @Builder
    public ApiDiaryResponse(boolean status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
