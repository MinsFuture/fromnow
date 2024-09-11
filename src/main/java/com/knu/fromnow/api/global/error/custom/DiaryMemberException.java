package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import lombok.Getter;

@Getter
public class DiaryMemberException extends RuntimeException{

    private DiaryMemberErrorCode diaryMemberErrorCode;

    public DiaryMemberException(DiaryMemberErrorCode diaryMemberErrorCode) {
        this.diaryMemberErrorCode = diaryMemberErrorCode;
    }
}
