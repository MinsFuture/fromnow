package com.knu.fromnow.api.domain.diary.entity;

import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.photo.entity.Photo;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Diarys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Diary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @OneToMany(mappedBy = "diary")
    private List<Photo> photoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime createdTime;

    @Builder
    public Diary(Member member, LocalDateTime createdTime) {
        this.member = member;
        this.createdTime = LocalDateTime.now();
    }
}
