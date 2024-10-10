package com.knu.fromnow.api.global.spec.date.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DateRequestDto {
    private int year;
    private int month;
    private int day;

    @Builder
    public DateRequestDto(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}