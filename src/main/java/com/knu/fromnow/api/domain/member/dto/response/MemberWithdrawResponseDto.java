package com.knu.fromnow.api.domain.member.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberWithdrawResponseDto {

    private Long id;
    private String profileName;

    @Builder
    public MemberWithdrawResponseDto(Long id, String profileName) {
        this.id = id;
        this.profileName = profileName;
    }

    public static MemberWithdrawResponseDto from(Member member){
        return MemberWithdrawResponseDto.builder()
                .id(member.getId())
                .profileName(member.getProfileName())
                .build();

    }
}
