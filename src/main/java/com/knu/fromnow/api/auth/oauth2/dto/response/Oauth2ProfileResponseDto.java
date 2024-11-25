package com.knu.fromnow.api.auth.oauth2.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Oauth2ProfileResponseDto {
    private String profileName;
    private boolean requiresAdditionalInfo;

    @Builder
    public Oauth2ProfileResponseDto(String profileName, boolean requiresAdditionalInfo) {
        this.profileName = profileName;
        this.requiresAdditionalInfo = requiresAdditionalInfo;
    }
}
