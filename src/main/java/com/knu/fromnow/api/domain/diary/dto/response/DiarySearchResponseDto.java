package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
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

    public static DiarySearchResponseDto of(Member member, boolean isTeam){
        return DiarySearchResponseDto.builder()
                .memberId(member.getId())
                .profilePhotoUrl(member.getPhotoUrl())
                .profileName(member.getProfileName())
                .isTeam(isTeam)
                .build();
    }
}
