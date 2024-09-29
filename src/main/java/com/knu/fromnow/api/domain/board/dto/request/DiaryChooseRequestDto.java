package com.knu.fromnow.api.domain.board.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DiaryChooseRequestDto {
    private String content;
    private List<Long> diaryIds;

    @Builder
    public DiaryChooseRequestDto(String content, List<Long> diaryIds) {
        this.content = content;
        this.diaryIds = diaryIds;
    }
}
