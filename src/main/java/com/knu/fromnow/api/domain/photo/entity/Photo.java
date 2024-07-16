package com.knu.fromnow.api.domain.photo.entity;

import com.knu.fromnow.api.domain.diary.entity.Board;
import com.knu.fromnow.api.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "photos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Photo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime createdTime;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Board board;

    @Builder
    public Photo(Member member, LocalDateTime createdTime, String photoUrl, Board board) {
        this.member = member;
        this.createdTime = LocalDateTime.now();
        this.photoUrl = photoUrl;
        this.board = board;
    }
}
