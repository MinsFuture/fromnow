package com.knu.fromnow.api.domain.read.dto;

import com.knu.fromnow.api.domain.read.entity.BoardRead;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardReadResponseDto {
    private Long boardId;
    private Long memberId;
    private LocalDateTime readAt;

    @Builder
    public BoardReadResponseDto(Long boardId, Long memberId, LocalDateTime readAt) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.readAt = readAt;
    }

    public static BoardReadResponseDto fromBoardRead(BoardRead boardRead){
        return BoardReadResponseDto.builder()
                .boardId(boardRead.getBoard().getId())
                .memberId(boardRead.getMember().getId())
                .readAt(boardRead.getReadAt())
                .build();
    }
}
