package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryMenuResponseDto {

    private Long memberId;
    private String profileName;
    private String photoUrl;
    private boolean isOwner;
    private boolean isFriend;

    @Builder
    public DiaryMenuResponseDto(Long memberId, String profileName, String photoUrl, boolean isOwner, boolean isFriend) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.photoUrl = photoUrl;
        this.isOwner = isOwner;
        this.isFriend = isFriend;
    }
}
