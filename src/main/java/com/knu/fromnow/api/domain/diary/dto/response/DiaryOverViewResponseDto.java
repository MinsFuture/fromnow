package com.knu.fromnow.api.domain.diary.dto.response;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiaryOverViewResponseDto {
    private Long id;
    private String title;
    private List<String> photoUrls;
    private String createdAt;
    private String recivedAt;

    @Builder
    public DiaryOverViewResponseDto(Long id, String title, List<String> photoUrls, String createdAt, String recivedAt) {
        this.id = id;
        this.title = title;
        this.photoUrls = photoUrls;
        this.createdAt = createdAt;
        this.recivedAt = recivedAt;
    }

    public static DiaryOverViewResponseDto of(Diary diary, List<String> photoUrls, LocalDateTime now){
        return DiaryOverViewResponseDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .photoUrls(photoUrls)
                .createdAt(diary.getCreatedAt().toString())
                .recivedAt(now.toString())
                .build();
    }
}
