package com.knu.fromnow.api.domain.friend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendDeleteResponseDto {
    private Long id;
    private String profileName;
    private String photoUrl;
    private boolean isFriend;

    @Builder
    public FriendDeleteResponseDto(Long id, String profileName, String photoUrl, boolean isFriend) {
        this.id = id;
        this.profileName = profileName;
        this.photoUrl = photoUrl;
        this.isFriend = isFriend;
    }
}
