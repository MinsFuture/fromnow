package com.knu.fromnow.api.domain.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TodayBoardResponseDto {

    private boolean isRead;
    private boolean isWrite;
    private List<BoardOverViewResponseDto> boardOverViewResponseDtoList;

    @Builder
    public TodayBoardResponseDto(boolean isRead, boolean isWrite, List<BoardOverViewResponseDto> boardOverViewResponseDtoList) {
        this.isRead = isRead;
        this.isWrite = isWrite;
        this.boardOverViewResponseDtoList = boardOverViewResponseDtoList;
    }
}
