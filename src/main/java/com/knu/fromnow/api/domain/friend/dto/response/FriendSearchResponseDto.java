package com.knu.fromnow.api.domain.friend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendSearchResponseDto {
    private String profileName;
    private String profilePhotoUrl;

    @Builder
    public FriendSearchResponseDto(String profileName, String profilePhotoUrl) {
        this.profileName = profileName;
        this.profilePhotoUrl = profilePhotoUrl;
    }

}
