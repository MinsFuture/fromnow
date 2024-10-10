package com.knu.fromnow.api.domain.board.dto.response;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class DiaryChooseResponseDto {
    private String content;
    private int likes;
    private String photoUrl;
    private List<Long> uploadDiaryIds;

    @Builder
    public DiaryChooseResponseDto(String content, int likes, String photoUrl, List<Long> uploadDiaryIds) {
        this.content = content;
        this.likes = likes;
        this.photoUrl = photoUrl;
        this.uploadDiaryIds = uploadDiaryIds;
    }

    public static DiaryChooseResponseDto fromBoard(Board board, List<Diary> diaryList){
        return DiaryChooseResponseDto.builder()
                .likes(board.getLike())
                .content(board.getContent())
                .photoUrl(board.getBoardPhoto().getPhotoUrl())
                .uploadDiaryIds(diaryList.stream().map(Diary::getId).toList())
                .build();
    }
}
