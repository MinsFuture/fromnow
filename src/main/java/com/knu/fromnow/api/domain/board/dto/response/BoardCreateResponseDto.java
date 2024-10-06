package com.knu.fromnow.api.domain.board.dto.response;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardCreateResponseDto {

    private Long id;
    private String content;
    private int likes;
    private String photoUrls;

    @Builder
    public BoardCreateResponseDto(Long id, String content, int likes, String photoUrls) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.photoUrls = photoUrls;
    }



    public static BoardCreateResponseDto fromBoard(Board board){
        return BoardCreateResponseDto.builder()
                .id(board.getId())
                .likes(board.getLike())
                .content(board.getContent())
                .photoUrls(board.getBoardPhoto().getPhotoUrl())
                .build();
    }
}
