package com.knu.fromnow.api.global.spec.firebase;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberNotificationStatusDto {
    private Long memberId;
    private String profileName;
    private boolean isNotificationSuccess;

    @Builder
    public MemberNotificationStatusDto(Long memberId, String profileName, boolean isNotificationSuccess) {
        this.memberId = memberId;
        this.profileName = profileName;
        this.isNotificationSuccess = isNotificationSuccess;
    }
}
