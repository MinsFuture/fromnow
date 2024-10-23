package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.LikeErrorCode;
import lombok.Getter;

@Getter
public class LikeException extends RuntimeException{
    private final LikeErrorCode likeErrorCode;

    public LikeException(LikeErrorCode likeErrorCode) {
        this.likeErrorCode = likeErrorCode;
    }

    public LikeException(String message, LikeErrorCode likeErrorCode) {
        super(message);
        this.likeErrorCode = likeErrorCode;
    }

    public LikeException(String message, Throwable cause, LikeErrorCode likeErrorCode) {
        super(message, cause);
        this.likeErrorCode = likeErrorCode;
    }

    public LikeException(Throwable cause, LikeErrorCode likeErrorCode) {
        super(cause);
        this.likeErrorCode = likeErrorCode;
    }

    public LikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, LikeErrorCode likeErrorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.likeErrorCode = likeErrorCode;
    }
}
