package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryImmeInviteResponseDto {
    private Long memberId;
    private String photoUrl;
    private String profileName;

    @Builder
    public DiaryImmeInviteResponseDto(Long memberId, String photoUrl, String profileName) {
        this.memberId = memberId;
        this.photoUrl = photoUrl;
        this.profileName = profileName;
    }

    public static DiaryImmeInviteResponseDto from(Member member){
        return DiaryImmeInviteResponseDto.builder()
                .memberId(member.getId())
                .photoUrl(member.getPhotoUrl())
                .profileName(member.getProfileName())
                .build();
    }
}
