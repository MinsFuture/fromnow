package com.knu.fromnow.api.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FcmResponseDto {
    private String fcmToken;

    @Builder
    public FcmResponseDto(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
