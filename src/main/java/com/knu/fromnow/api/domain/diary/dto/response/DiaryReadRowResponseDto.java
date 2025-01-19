package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiaryReadRowResponseDto {

    private boolean isNew;
    private boolean hasPosts;
    private String date;

    @Builder
    public DiaryReadRowResponseDto(boolean isNew, boolean hasPosts, String date) {
        this.isNew = isNew;
        this.hasPosts = hasPosts;
        this.date = date;
    }

    public static DiaryReadRowResponseDto of(boolean isNew, boolean hasPosts, LocalDate date){
        return DiaryReadRowResponseDto.builder()
                .isNew(isNew)
                .hasPosts(hasPosts)
                .date(date.toString())
                .build();
    }
}
