package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryCreateResponseDto {

    private Long id;
    private String title;
    private String photoUrls;

    @Builder
    public DiaryCreateResponseDto(Long id, String title, String photoUrls) {
        this.id = id;
        this.title = title;
        this.photoUrls = photoUrls;
    }




    public static DiaryCreateResponseDto fromDiary(Diary diary){
        return DiaryCreateResponseDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .photoUrls(diary.getOwner().getPhotoUrl())
                .build();

    }

}
