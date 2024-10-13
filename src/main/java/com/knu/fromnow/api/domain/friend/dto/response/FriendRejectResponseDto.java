package com.knu.fromnow.api.domain.friend.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendRejectResponseDto {
    private Long memberId;
    private String profileName;
    private String photoUrl;
    private boolean isFriend;

    @Builder
    public FriendRejectResponseDto(Long memberId, String profileName, String photoUrl, boolean isFriend) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.photoUrl = photoUrl;
        this.isFriend = isFriend;
    }

    public static FriendRejectResponseDto makeFrom(Member member){
        return FriendRejectResponseDto.builder()
                .memberId(member.getId())
                .profileName(member.getProfileName())
                .photoUrl(member.getPhotoUrl())
                .isFriend(false)
                .build();
    }
}
