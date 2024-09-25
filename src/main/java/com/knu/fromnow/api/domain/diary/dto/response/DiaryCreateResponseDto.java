package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryCreateResponseDto {

    private Long id;
    private String title;
    private String photoUrl;

    @Builder
    public DiaryCreateResponseDto(Long id, String title, String photoUrl) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
    }

    public static DiaryCreateResponseDto fromDiary(Diary diary){
        return DiaryCreateResponseDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .photoUrl(diary.getOwner().getPhotoUrl())
                .build();

    }

}
