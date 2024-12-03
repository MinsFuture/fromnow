package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements ErrorCode {
    NEVER_CLICK_THE_LIKE_BUTTON_EXCEPTION(HttpStatus.BAD_REQUEST, "좋아요를 누른적이 없는 게시글 입니다" ),
    ALREADY_CLICK_LIKE_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 적이 있는 게시글 입니다");

    LikeErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;
}
