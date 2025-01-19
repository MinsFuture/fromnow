package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryDeleteResponseDto {

    private Long id;

    @Builder
    public DiaryDeleteResponseDto(Long id) {
        this.id = id;
    }

    public static DiaryDeleteResponseDto from(Diary diary){
        return DiaryDeleteResponseDto.builder()
                .id(diary.getId())
                .build();
    }
}
