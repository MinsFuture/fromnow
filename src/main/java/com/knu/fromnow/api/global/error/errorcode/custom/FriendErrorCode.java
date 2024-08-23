package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FriendErrorCode implements ErrorCode {

    ALREADY_WE_ARE_FRIEND_EXCEPTION(HttpStatus.CONFLICT, "이미 서로 친구입니다"),
    INVALID_FRIEND_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "친구 수락 Api request 형식이 잘못 되었습니다");

    FriendErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;
}
