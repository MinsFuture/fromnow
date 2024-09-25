package com.knu.fromnow.api.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PhotoUrlResponseDto {
    private String photoUrl;

    @Builder
    public PhotoUrlResponseDto(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
