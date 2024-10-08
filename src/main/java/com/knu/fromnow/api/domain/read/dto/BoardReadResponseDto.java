package com.knu.fromnow.api.domain.read.dto;

import com.knu.fromnow.api.domain.read.entity.BoardRead;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardReadResponseDto {
    private Long boardId;
    private Long memberId;

    @Builder
    public BoardReadResponseDto(Long boardId, Long memberId) {
        this.boardId = boardId;
        this.memberId = memberId;
    }

    public static BoardReadResponseDto fromBoardRead(BoardRead boardRead){
        return BoardReadResponseDto.builder()
                .boardId(boardRead.getBoard().getId())
                .memberId(boardRead.getMember().getId())
                .build();
    }
}
