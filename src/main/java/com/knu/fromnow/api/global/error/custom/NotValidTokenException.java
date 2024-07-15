package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.auth.jwt.dto.response.TokenStatus;
import com.knu.fromnow.api.global.error.errorcode.JwtTokenErrorCode;

public class NotValidTokenException extends RuntimeException{

    private JwtTokenErrorCode errorCode;
    private TokenStatus tokenStatus;

    public NotValidTokenException(JwtTokenErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public NotValidTokenException(JwtTokenErrorCode errorCode, TokenStatus tokenStatus) {
        this.errorCode = errorCode;
        this.tokenStatus = tokenStatus;
    }
}
