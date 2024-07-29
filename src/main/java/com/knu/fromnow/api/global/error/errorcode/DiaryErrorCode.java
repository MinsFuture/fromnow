package com.knu.fromnow.api.global.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode {

    NO_EXIST_DIARY_EXCEPTION(404, "요청 id에 해당하는 diary가 없습니다");

    DiaryErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
