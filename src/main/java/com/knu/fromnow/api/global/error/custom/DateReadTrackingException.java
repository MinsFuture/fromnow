package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
import lombok.Getter;

@Getter
public class DateReadTrackingException extends RuntimeException{

    private DateReadTrackingErrorCode dateReadTrackingErrorCode;

    public DateReadTrackingException(DateReadTrackingErrorCode dateReadTrackingErrorCode) {
        this.dateReadTrackingErrorCode = dateReadTrackingErrorCode;
    }
}
