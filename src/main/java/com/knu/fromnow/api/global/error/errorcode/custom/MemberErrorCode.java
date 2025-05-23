package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {

    NO_EXIST_MEMBER_ID_EXCEPTION(HttpStatus.NOT_FOUND, "해당 Member Id에 해당하는 Member가 없습니다"),
    No_EXIST_EMAIL_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "email에 해당하는 member가 없습니다"),
    NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "profileName에 해당하는 member가 없습니다"),
    CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 profileName 입니다"),
    NO_MATCHING_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 다이어리에 소속된 멤버가 아닙니다. 다이어리를 조회 할 권한이 없습니다"),
    NO_OWNER_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 다이어리의 Owner가 아닙니다."),
    ALREADY_INVITED_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 다이어리에 소속된 멤버를 초대하고 있습니다"),
    CANNOT_DELETE_MEMBER(HttpStatus.BAD_REQUEST, "자기 자신만 탈퇴 할 수 있습니다"),
    CANNOT_LOGOUT_MEMBER(HttpStatus.BAD_REQUEST, "자기 자신만 로그아웃 할 수 있습니다");


    MemberErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;

}
