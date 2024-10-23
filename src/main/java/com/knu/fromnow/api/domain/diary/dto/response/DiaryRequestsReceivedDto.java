package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiaryRequestsReceivedDto {
    private String diaryTitle;
    private Long diaryId;
    private List<String> photoUrls;

    @Builder
    public DiaryRequestsReceivedDto(String diaryTitle, Long diaryId, List<String> photoUrls) {
        this.diaryTitle = diaryTitle;
        this.diaryId = diaryId;
        this.photoUrls = photoUrls;
    }
}
