package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.DiaryType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryOverViewResponseDto {
    private Long id;
    private String title;
    private DiaryType diaryType;

    @Builder
    public DiaryOverViewResponseDto(Long id, String title, DiaryType diaryType) {
        this.id = id;
        this.title = title;
        this.diaryType = diaryType;
    }
}
