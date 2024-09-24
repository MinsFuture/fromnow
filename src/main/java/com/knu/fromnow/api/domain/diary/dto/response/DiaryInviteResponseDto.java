package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryInviteResponseDto {

    private Long memberId;
    private String photoUrl;
    private String profileName;

    @Builder
    public DiaryInviteResponseDto(Long memberId, String photoUrl, String profileName) {
        this.memberId = memberId;
        this.photoUrl = photoUrl;
        this.profileName = profileName;
    }

    public static DiaryInviteResponseDto makeFrom(Member member){
        return DiaryInviteResponseDto.builder()
                .memberId(member.getId())
                .photoUrl(member.getPhotoUrl())
                .profileName(member.getProfileName())
                .build();
    }
}
