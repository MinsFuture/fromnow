package com.knu.fromnow.api.domain.diary.entity;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.member.entity.Member;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "diarys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    private String title;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE)
    private List<DiaryMember> diaryMembers = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE)
    private List<Board> boardList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @Builder
    public Diary(String title, Member owner) {
        this.title = title;
        this.owner = owner;
    }

    public static Diary createBoard(CreateDiaryDto createDiaryDto, Member owner) {
        return Diary.builder()
                .title(createDiaryDto.getTitle())
                .owner(owner)
                .build();
    }

    public void updateDiaryTitle(UpdateDiaryDto updateDiaryDto) {
        this.title = updateDiaryDto.getNewTitle();
    }

    public void addBoard(Board board) {
        this.boardList.add(board);
        if (board.getDiary() != this) {
            board.setDiary(this);
        }
    }
}
