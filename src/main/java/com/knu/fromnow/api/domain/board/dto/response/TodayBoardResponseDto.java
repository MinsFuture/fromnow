package com.knu.fromnow.api.domain.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TodayBoardResponseDto {

    private boolean isRead;
    private boolean isBlur;
    private List<BoardOverViewResponseDto> boardOverViewResponseDtoList;

    @Builder
    public TodayBoardResponseDto(boolean isRead, boolean isBlur, List<BoardOverViewResponseDto> boardOverViewResponseDtoList) {
        this.isRead = isRead;
        this.isBlur = isBlur;
        this.boardOverViewResponseDtoList = boardOverViewResponseDtoList;
    }

    public static TodayBoardResponseDto of(boolean isBlur, boolean isRead, List<BoardOverViewResponseDto> boardOverViewResponseDtos){
        return TodayBoardResponseDto.builder()
                .isBlur(isBlur)
                .isRead(isRead)
                .boardOverViewResponseDtoList(boardOverViewResponseDtos)
                .build();
    }
}
