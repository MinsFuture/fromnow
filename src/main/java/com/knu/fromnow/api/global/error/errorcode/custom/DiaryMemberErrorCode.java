package com.knu.fromnow.api.global.error.errorcode.custom;

import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DiaryMemberErrorCode implements ErrorCode {

    NO_EXIST_DIARY_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 멤버와 다이어리에 해당하는 엔티티가 없습니다"),
    ALREADY_ACCEPTED_INVITATION_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 초대를 수락한 유저입니다");

   DiaryMemberErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;
}
