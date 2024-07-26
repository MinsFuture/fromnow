package com.knu.fromnow.api.domain.diary.entity;

import com.knu.fromnow.api.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Diarys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Diary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    private Member owner;

    @OneToMany(mappedBy = "diary")
    private List<DiaryMember> diaryMembers = new ArrayList<>();

    @Builder
    public Diary(Member owner, String title) {
        this.owner = owner;
        this.title = title;
    }
}
