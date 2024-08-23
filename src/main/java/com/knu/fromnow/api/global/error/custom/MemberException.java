package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException{

    private MemberErrorCode memberErrorCode;

    public MemberException() {
    }

    public MemberException(String message) {
        super(message);
    }

    public MemberException(MemberErrorCode memberErrorCode) {
        this.memberErrorCode = memberErrorCode;
    }
}
