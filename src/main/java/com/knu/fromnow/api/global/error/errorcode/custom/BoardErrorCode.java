package com.knu.fromnow.api.global.error.errorcode.custom;


import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {

    NO_EXIST_BOARD_EXCEPTION(HttpStatus.NOT_FOUND, "요청 id에 해당하는 Board가 없습니다");

    BoardErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;
    private String message;
}
