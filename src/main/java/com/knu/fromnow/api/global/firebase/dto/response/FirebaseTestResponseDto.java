package com.knu.fromnow.api.global.firebase.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FirebaseTestResponseDto {
    private String message;

    @Builder
    public FirebaseTestResponseDto(String message) {
        this.message = message;
    }
}
