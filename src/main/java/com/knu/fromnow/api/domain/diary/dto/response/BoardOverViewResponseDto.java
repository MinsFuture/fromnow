package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class BoardOverViewResponseDto {
    private Long boardId;
    private String createdDate;
    private String profilePhotoUrl;
    private String profileName;
    private List<String> contentPhotoUrl;
    private String content;

    @Builder
    public BoardOverViewResponseDto(Long boardId, String createdDate, String profilePhotoUrl, String profileName, List<String> contentPhotoUrl, String content) {
        this.boardId = boardId;
        this.createdDate = createdDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.profileName = profileName;
        this.contentPhotoUrl = contentPhotoUrl;
        this.content = content;
    }
}
