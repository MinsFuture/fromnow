package com.knu.fromnow.api.domain.friend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendBasicResponseDto {
    private Long memberId;
    private String profileName;
    private String profilePhotoUrl;
    private boolean isFriend;

    @Builder
    public FriendBasicResponseDto(Long memberId, String profileName, String profilePhotoUrl, boolean isFriend) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isFriend = isFriend;
    }
}
