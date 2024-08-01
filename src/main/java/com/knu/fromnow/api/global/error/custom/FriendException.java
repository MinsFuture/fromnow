package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.FriendErrorCode;
import lombok.Getter;

@Getter
public class FriendException extends RuntimeException{

    private FriendErrorCode friendErrorCode;

    public FriendException(FriendErrorCode friendErrorCode) {
        this.friendErrorCode = friendErrorCode;
    }
}
