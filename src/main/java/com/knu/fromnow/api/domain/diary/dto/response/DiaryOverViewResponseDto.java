package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryOverViewResponseDto {
    private Long id;
    private String title;
    private List<String> photoUrls;

    @Builder
    public DiaryOverViewResponseDto(Long id, String title,  List<String> photoUrls) {
        this.id = id;
        this.title = title;
        this.photoUrls = photoUrls;
    }
}
