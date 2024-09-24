package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryCreateResponseDto {

    private String title;

    @Builder
    public DiaryCreateResponseDto(String title) {
        this.title = title;
    }

    public static DiaryCreateResponseDto fromDiary(Diary diary){
        return DiaryCreateResponseDto.builder()
                .title(diary.getTitle())
                .build();

    }

}
