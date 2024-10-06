package com.knu.fromnow.api.domain.board.dto.response;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class DiaryChooseResponseDto {
    private Long id;
    private String content;
    private int likes;
    private String photoUrl;
    private List<Long> uploadDiaryIds;

    @Builder
    public DiaryChooseResponseDto(Long id, String content, int likes, String photoUrl, List<Long> uploadDiaryIds) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.photoUrl = photoUrl;
        this.uploadDiaryIds = uploadDiaryIds;
    }

    public static DiaryChooseResponseDto fromBoard(Board board, List<Diary> diaryList){
        return DiaryChooseResponseDto.builder()
                .id(board.getId())
                .likes(board.getLike())
                .content(board.getContent())
                .photoUrl(board.getBoardPhoto().getPhotoUrl())
                .uploadDiaryIds(diaryList.stream().map(Diary::getId).toList())
                .build();
    }
}
