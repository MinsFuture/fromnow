package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import lombok.Getter;

@Getter
public class DiaryException extends RuntimeException{

    private DiaryErrorCode diaryErrorCode;

    public DiaryException(DiaryErrorCode diaryErrorCode) {
        this.diaryErrorCode = diaryErrorCode;
    }

    public DiaryException(String message, DiaryErrorCode diaryErrorCode) {
        super(message);
        this.diaryErrorCode = diaryErrorCode;
    }
}
