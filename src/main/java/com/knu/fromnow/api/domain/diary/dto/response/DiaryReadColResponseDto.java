package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryReadColResponseDto {

    private String date;
    private boolean isBlur;
    private List<String> photoUrls;

    @Builder
    public DiaryReadColResponseDto(String date, boolean isBlur, List<String> photoUrls) {
        this.date = date;
        this.isBlur = isBlur;
        this.photoUrls = photoUrls;
    }
}
