package com.knu.fromnow.api.global.spec.date.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class YearMonthRequestDto {
    private int year;
    private int month;

    @Builder
    public YearMonthRequestDto(int year, int month) {
        this.year = year;
        this.month = month;
    }
}
