package com.knu.fromnow.api.domain.member.dto.response;

import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class ProfileMemberDto {
    private String profileName;
    private String photoUrl;

    @Builder
    public ProfileMemberDto(String profileName, String photoUrl) {
        this.profileName = profileName;
        this.photoUrl = photoUrl;
    }

    public static ProfileMemberDto from(Member member){
        return ProfileMemberDto.builder()
                .profileName(member.getProfileName())
                .photoUrl(member.getPhotoUrl())
                .build();
    }
}
