package com.knu.fromnow.api.domain.like.entity;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "likes_column")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private boolean isLiked;

    @Builder
    public Like(Member member, Board board, boolean isLiked) {
        this.member = member;
        this.board = board;
        this.isLiked = isLiked;
    }

    public void disLike(){
        this.isLiked = false;
    }

    public static Like initLike(Board board, Member member){
        return Like.builder()
                .board(board)
                .member(member)
                .isLiked(true)
                .build();
    }
}
