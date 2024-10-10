package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DateReadTrackingErrorCode implements ErrorCode {

    NO_DATE_READ_TRACKING_EXIST_EXCEPTION(HttpStatus.NOT_FOUND, "해당 요청에 해당하는 DateReadTracking이 없습니다");

    DateReadTrackingErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;



}
