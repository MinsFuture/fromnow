package com.knu.fromnow.api.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileNameResponseDto {

    private String profileName;

    @Builder
    public ProfileNameResponseDto(String profileName) {
        this.profileName = profileName;
    }
}
