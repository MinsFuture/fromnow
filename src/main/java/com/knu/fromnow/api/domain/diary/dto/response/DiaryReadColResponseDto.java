package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryReadColResponseDto {

    private String date;
    private List<DiaryPhotoUrlWithBlurDto> photoUrlWithBlurDtos;

    @Builder
    public DiaryReadColResponseDto(String date, List<DiaryPhotoUrlWithBlurDto> photoUrlWithBlurDtos) {
        this.date = date;
        this.photoUrlWithBlurDtos = photoUrlWithBlurDtos;
    }
}
