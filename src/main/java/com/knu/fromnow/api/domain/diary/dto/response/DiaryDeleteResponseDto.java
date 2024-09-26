package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryDeleteResponseDto {

    private Long id;

    @Builder
    public DiaryDeleteResponseDto(Long id) {
        this.id = id;
    }
}
