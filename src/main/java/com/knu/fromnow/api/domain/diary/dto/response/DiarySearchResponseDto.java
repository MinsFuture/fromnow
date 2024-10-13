package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiarySearchResponseDto {

    private Long memberId;
    private String profileName;
    private String profilePhotoUrl;
    private boolean isTeam;

    @Builder
    public DiarySearchResponseDto(Long memberId, String profileName, String profilePhotoUrl, boolean isTeam) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isTeam = isTeam;
    }
}
