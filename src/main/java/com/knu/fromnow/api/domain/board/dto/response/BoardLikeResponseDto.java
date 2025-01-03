package com.knu.fromnow.api.domain.board.dto.response;

import com.knu.fromnow.api.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardLikeResponseDto {
    private int likes;

    @Builder
    public BoardLikeResponseDto(int likes) {
        this.likes = likes;
    }

    public static BoardLikeResponseDto from(Board board){
        return BoardLikeResponseDto.builder()
                .likes(board.getLike())
                .build();
    }
}
