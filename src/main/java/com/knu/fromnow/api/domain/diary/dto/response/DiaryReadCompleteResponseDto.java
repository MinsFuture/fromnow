package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiaryReadCompleteResponseDto {

    private Long diaryId;
    private String date;
    private boolean isRead;

    @Builder
    public DiaryReadCompleteResponseDto(Long diaryId, String date, boolean isRead) {
        this.diaryId = diaryId;
        this.date = date;
        this.isRead = isRead;
    }

    public static DiaryReadCompleteResponseDto of(Long diaryId, LocalDate date){
       return DiaryReadCompleteResponseDto
                .builder()
                .diaryId(diaryId)
                .date(date.toString())
                .isRead(true)
                .build();
    }
}
