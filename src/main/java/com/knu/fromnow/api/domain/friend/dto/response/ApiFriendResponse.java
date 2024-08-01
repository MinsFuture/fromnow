package com.knu.fromnow.api.domain.friend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiFriendResponse<T> {

    private boolean status;
    private int code;
    private String message;
    private T data;

    @Builder
    public ApiFriendResponse(boolean status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
