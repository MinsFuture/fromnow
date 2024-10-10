package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
import lombok.Builder;
import lombok.Getter;

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
}
