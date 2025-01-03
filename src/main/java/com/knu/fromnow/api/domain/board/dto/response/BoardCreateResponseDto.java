package com.knu.fromnow.api.domain.board.dto.response;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardCreateResponseDto {

    private Long id;
    private String content;
    private int likes;
    private String photoUrls;
    private List<MemberNotificationStatusDto> notificationStatuses;  // 멤버별 알림 성공 여부 리스트

    @Builder
    public BoardCreateResponseDto(Long id, String content, int likes, String photoUrls, List<MemberNotificationStatusDto> notificationStatuses) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.photoUrls = photoUrls;
        this.notificationStatuses = notificationStatuses;
    }

    public static BoardCreateResponseDto of(Board board, List<MemberNotificationStatusDto> memberNotificationStatusDtos){
        return BoardCreateResponseDto.builder()
                .id(board.getId())
                .likes(board.getLike())
                .content(board.getContent())
                .photoUrls(board.getBoardPhoto().getPhotoUrl())
                .notificationStatuses(memberNotificationStatusDtos)
                .build();
    }
}
