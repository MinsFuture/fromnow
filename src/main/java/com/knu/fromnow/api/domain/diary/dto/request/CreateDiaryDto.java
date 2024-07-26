package com.knu.fromnow.api.domain.diary.dto.request;

import com.knu.fromnow.api.domain.diary.entity.DiaryType;
import lombok.Getter;

@Getter
public class CreateDiaryDto {
    private String title;
    private DiaryType diaryType;
}
