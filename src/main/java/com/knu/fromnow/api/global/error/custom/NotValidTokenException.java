package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.JwtTokenErrorCode;
import lombok.Getter;

@Getter
public class NotValidTokenException extends RuntimeException{

    private JwtTokenErrorCode errorCode;

    public NotValidTokenException(JwtTokenErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
