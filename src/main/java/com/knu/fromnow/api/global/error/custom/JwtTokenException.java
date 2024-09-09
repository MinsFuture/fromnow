package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.JwtTokenErrorCode;
import lombok.Getter;

@Getter
public class JwtTokenException extends RuntimeException{

    private JwtTokenErrorCode jwtTokenErrorCode;

    public JwtTokenException(JwtTokenErrorCode jwtTokenErrorCode) {
        this.jwtTokenErrorCode = jwtTokenErrorCode;
    }
}
