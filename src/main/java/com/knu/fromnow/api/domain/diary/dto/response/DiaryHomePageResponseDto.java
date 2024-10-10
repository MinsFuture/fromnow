package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryHomePageResponseDto {
    private String date;
    private List<DiaryOverViewResponseDto> diaryOverViewResponseDtos;

    @Builder
    public DiaryHomePageResponseDto(String date, List<DiaryOverViewResponseDto> diaryOverViewResponseDtos) {
        this.date = date;
        this.diaryOverViewResponseDtos = diaryOverViewResponseDtos;
    }
}
