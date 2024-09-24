package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.auth.jwt.dto.response.TokenStatus;
import com.knu.fromnow.api.global.error.errorcode.DataErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.knu.fromnow.api.auth.jwt.dto.response.TokenStatus.*;

@Getter
@RequiredArgsConstructor
public enum JwtTokenErrorCode implements DataErrorCode<String> {

    //401 JWT 인증 관련 오류
    NO_EXIST_AUTHORIZATION_HEADER_EXCEPTION(HttpStatus.BAD_REQUEST, "Authorization 헤더가 없거나 Bearer로 시작하지 않습니다", INVALID),
    EXPIRED_ACCESS_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "Access token이 만료되었습니다. Refresh token을 사용하세요.", ACCESS_TOKEN_EXPIRED),

    EXPIRED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "Refresh token이 만료되었습니다. 다시 로그인하세요.", REFRESH_TOKEN_EXPIRED),

    NOT_VALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "유효하지 않은 JWT 토큰입니다.", INVALID),

    UNSUPPORTED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "지원되지 않는 JWT 토큰입니다.", UNSUPPORTED),

    MISMATCH_CLAIMS_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "JWT 토큰의 클레임이 일치하지 않거나 토큰이 없습니다.", MISMATCH_CLAIMS),

    REFRESH_TOKEN_COOKIE_NULL_EXCEPTION(HttpStatus.BAD_REQUEST,
            "쿠키에 담긴 Refresh token이 null 입니다", COOKIE_NULL),

    REFRESH_TOKEN_MISMATCH_EXCEPTION(HttpStatus.UNAUTHORIZED,
            "해당 Refresh token과 DB에 저장된 member의 token이 일치하는게 없습니다. 다시 로그인하세요.", REFRESH_TOKEN_MISMATCH);


    private final HttpStatus httpStatus;
    private final String message;
    private final TokenStatus tokenStatus;

    @Override
    public String getData() {
        return tokenStatus.name();
    }
}