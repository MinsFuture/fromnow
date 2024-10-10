package com.knu.fromnow.api.domain.board.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class BoardOverViewResponseDto {
    private Long boardId;
    private String createdDate;
    private String profilePhotoUrl;
    private String profileName;
    private String contentPhotoUrl;
    private String content;
    private int likes;
    private boolean isLiked;

    @Builder
    public BoardOverViewResponseDto(Long boardId, String createdDate, String profilePhotoUrl, String profileName, String contentPhotoUrl, String content, int likes, boolean isLiked) {
        this.boardId = boardId;
        this.createdDate = createdDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.profileName = profileName;
        this.contentPhotoUrl = contentPhotoUrl;
        this.content = content;
        this.likes = likes;
        this.isLiked = isLiked;
    }
}
