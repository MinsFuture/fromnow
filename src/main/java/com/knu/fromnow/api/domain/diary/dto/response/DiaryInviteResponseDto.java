package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public static List<DiaryInviteResponseDto> makeFrom(List<Member> members){

        List<DiaryInviteResponseDto> list = new ArrayList<>();

        for (Member member : members) {
            list.add(DiaryInviteResponseDto.builder()
                    .memberId(member.getId())
                    .photoUrl(member.getPhotoUrl())
                    .profileName(member.getProfileName())
                    .build());
        }

        return list;
    }
}
