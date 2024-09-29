package com.knu.fromnow.api.auth.oauth2.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Oauth2ProfileResponseDto {
    private String profileName;

    @Builder
    public Oauth2ProfileResponseDto(String profileName) {
        this.profileName = profileName;
    }
}
