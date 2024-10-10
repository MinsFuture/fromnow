package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.DateLatestPostTimeErrorCode;
import lombok.Getter;

@Getter
public class DateLatestPostTimeException extends RuntimeException{

    private DateLatestPostTimeErrorCode dateLatestPostTimeErrorCode;

    public DateLatestPostTimeException(DateLatestPostTimeErrorCode dateLatestPostTimeErrorCode) {
        this.dateLatestPostTimeErrorCode = dateLatestPostTimeErrorCode;
    }
}
