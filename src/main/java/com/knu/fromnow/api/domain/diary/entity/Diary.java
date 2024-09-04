package com.knu.fromnow.api.domain.diary.entity;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "diarys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Diary extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private DiaryType diaryType;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE)
    private List<DiaryMember> diaryMembers = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE)
    private List<Board> boardList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @Builder
    public Diary(DiaryType diaryType, String title, Member owner) {
        this.diaryType = diaryType;
        this.title = title;
        this.owner = owner;
    }

    public void updateDiaryTitle(UpdateDiaryDto updateDiaryDto){
        this.title = updateDiaryDto.getNewTitle();
    }
}
