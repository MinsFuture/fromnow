package com.knu.fromnow.api.domain.diary.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class InviteToDiaryDto {
    private Long diaryId;
    private List<String> profileNames;
}
