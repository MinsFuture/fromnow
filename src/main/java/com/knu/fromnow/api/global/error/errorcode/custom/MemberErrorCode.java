package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {

    No_EXIST_EMAIL_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "email에 해당하는 member가 없습니다"),
    NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "profileName에 해당하는 member가 없습니다"),
    CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 profileName 입니다"),
    NO_MATCHING_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 다이어리에 소속된 멤버가 아닙니다. 다이어리를 조회 할 권한이 없습니다"),
    NO_OWNER_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 다이어리의 Owner가 아닙니다.");

    MemberErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;

}
