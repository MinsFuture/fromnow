package com.knu.fromnow.api.domain.board.entity;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "boards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String content;

    @Column(name = "board_likes")
    private int like = 0;

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL)
    private BoardPhoto boardPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Like> likeList = new ArrayList<>();

    @Builder
    public Board(String content, int like, Diary diary, Member member) {
        this.content = content;
        this.like = like;
        this.diary = diary;
        this.member = member;
    }

    public void likeBoard(){
        this.like++;
    }

    public void dislikeBoard(){
        if(this.like > 0){
            this.like--;
        }
    }

    public void uploadBoardPhoto(BoardPhoto boardPhoto){
        this.boardPhoto = boardPhoto;
    }
}
