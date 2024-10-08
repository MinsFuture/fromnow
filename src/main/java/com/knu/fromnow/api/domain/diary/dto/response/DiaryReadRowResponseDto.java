package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryReadRowResponseDto {

    private boolean isNew;
    private boolean hasPosts;
    private int year;
    private int month;
    private int day;

    @Builder
    public DiaryReadRowResponseDto(boolean isNew, boolean hasPosts, int year, int month, int day) {
        this.isNew = isNew;
        this.hasPosts = hasPosts;
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
