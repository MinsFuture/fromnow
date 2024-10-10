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
    private String date;

    @Builder
    public DiaryOverViewResponseDto(Long id, String title, List<String> photoUrls, String date) {
        this.id = id;
        this.title = title;
        this.photoUrls = photoUrls;
        this.date = date;
    }

    public static DiaryOverViewResponseDto makeFrom(Diary diary) {
        return DiaryOverViewResponseDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .date(diary.getCreatedAt().toString())
                .build();
    }
}
