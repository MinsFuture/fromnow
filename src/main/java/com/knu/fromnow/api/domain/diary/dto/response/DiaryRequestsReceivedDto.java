package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryRequestsReceivedDto {
    private String diaryTitle;
    private Long diaryId;

    @Builder
    public DiaryRequestsReceivedDto(String diaryTitle, Long diaryId) {
        this.diaryTitle = diaryTitle;
        this.diaryId = diaryId;
    }
}
