package com.knu.fromnow.api.domain.photo.entity;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.global.BaseEntity;
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

@Entity(name = "board_photos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BoardPhoto extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_photo_id")
    private Long id;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public BoardPhoto(String photoUrl, Board board) {
        this.photoUrl = photoUrl;
        this.board = board;
    }
}
