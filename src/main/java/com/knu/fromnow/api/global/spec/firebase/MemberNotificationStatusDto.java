package com.knu.fromnow.api.global.spec.firebase;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberNotificationStatusDto {
    private Long memberId;
    private String profileName;
    private String fcmToken;
    private boolean isNotificationSuccess;
    private String errorMessage;

    @Builder
    public MemberNotificationStatusDto(Long memberId, String profileName, String fcmToken, boolean isNotificationSuccess, String errorMessage) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.fcmToken = fcmToken;
        this.isNotificationSuccess = isNotificationSuccess;
        this.errorMessage = errorMessage;
    }
}
