package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DateLatestPostTimeErrorCode implements ErrorCode {

    NO_DATE_LATEST_POST_TIME_EXCEPTION(HttpStatus.NOT_FOUND, "해당 날짜와 diaryId에 해당하는 추적 정보가 없습니다");

    DateLatestPostTimeErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;
}
