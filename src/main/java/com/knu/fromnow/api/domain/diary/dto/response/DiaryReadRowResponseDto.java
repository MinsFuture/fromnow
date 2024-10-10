package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

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
}
