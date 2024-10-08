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
    private boolean isRead;

    @Builder
    public BoardOverViewResponseDto(Long boardId, String createdDate, String profilePhotoUrl, String profileName, String contentPhotoUrl, String content, boolean isRead) {
        this.boardId = boardId;
        this.createdDate = createdDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.profileName = profileName;
        this.contentPhotoUrl = contentPhotoUrl;
        this.content = content;
        this.isRead = isRead;
    }
}
