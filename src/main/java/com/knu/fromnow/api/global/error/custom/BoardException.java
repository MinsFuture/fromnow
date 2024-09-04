package com.knu.fromnow.api.global.error.custom;

import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import lombok.Getter;

@Getter
public class BoardException extends RuntimeException{

    private BoardErrorCode boardErrorCode;

    public BoardException(BoardErrorCode boardErrorCode) {
        this.boardErrorCode = boardErrorCode;
    }

    public BoardException(String message, BoardErrorCode boardErrorCode) {
        super(message);
        this.boardErrorCode = boardErrorCode;
    }
}
