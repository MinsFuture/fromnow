package com.knu.fromnow.api.domain.diary.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryPhotoUrlWithBlurDto {
    private String photoUrl;
    private boolean isBlur;

    @Builder
    public DiaryPhotoUrlWithBlurDto(String photoUrl, boolean isBlur) {
        this.photoUrl = photoUrl;
        this.isBlur = isBlur;
    }
}
