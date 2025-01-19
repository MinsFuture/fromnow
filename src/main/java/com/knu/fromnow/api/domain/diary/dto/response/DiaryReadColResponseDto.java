package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
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


    public static DiaryReadColResponseDto of(LocalDate date, boolean hasWritten, List<String> photoUrls) {
        return DiaryReadColResponseDto.builder()
                .date(date.toString())
                .isBlur(!hasWritten)
                .photoUrls(photoUrls)
                .build();
    }
}
