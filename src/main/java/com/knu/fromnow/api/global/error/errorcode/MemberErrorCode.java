package com.knu.fromnow.api.global.error.errorcode;

import com.knu.fromnow.api.domain.member.entity.Role;
import lombok.Getter;

@Getter
public enum MemberErrorCode {

    No_EXIST_EMAIL_MEMBER_EXCEPTION(404, "email에 해당하는 member가 없습니다"),
    NO_EXIST_IDENTIFIER_MEMBER_EXCEPTION(404, "identifier에 해당하는 member가 없습니다"),
    CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION(409, "이미 존재하는 profileName 입니다");

    MemberErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    MemberErrorCode(int code, String message, Role type) {
        this.code = code;
        this.message = message;
        this.type = type;
    }

    private int code;
    private String message;
    private Role type;
}
