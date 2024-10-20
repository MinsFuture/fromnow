package com.knu.fromnow.api.domain.diary.entity;

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

import java.time.LocalDateTime;

@Entity(name = "Diary_Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DiaryMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime recievedAt;

    private boolean acceptedInvite;

    @Builder
    public DiaryMember(Diary diary, Member member, LocalDateTime recievedAt, boolean acceptedInvite) {
        this.diary = diary;
        this.member = member;
        this.recievedAt = recievedAt;
        this.acceptedInvite = acceptedInvite;
    }

    public void acceptInvitation(){
        this.acceptedInvite = true;
    }

    public void updateRecievedAt(LocalDateTime now) {
        this.recievedAt = now;
    }
}
