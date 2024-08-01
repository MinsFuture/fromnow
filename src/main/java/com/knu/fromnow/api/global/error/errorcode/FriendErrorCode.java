package com.knu.fromnow.api.global.error.errorcode;

import lombok.Getter;

@Getter
public enum FriendErrorCode {

    ALREADY_WE_ARE_FRIEND_EXCEPTION(409, "이미 서로 친구입니다"),
    INVALID_FRIEND_REQUEST_EXCEPTION(404, "친구 수락 Api request 형식이 잘못 되었습니다");

    FriendErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
