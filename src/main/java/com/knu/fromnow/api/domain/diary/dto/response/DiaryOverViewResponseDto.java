package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryOverViewResponseDto {
    private Long id;
    private String title;
    private List<String> photoUrls;
    private String createdAt;
    private String recivedAt;

    @Builder
    public DiaryOverViewResponseDto(Long id, String title, List<String> photoUrls, String createdAt, String recivedAt) {
        this.id = id;
        this.title = title;
        this.photoUrls = photoUrls;
        this.createdAt = createdAt;
        this.recivedAt = recivedAt;
    }
}
