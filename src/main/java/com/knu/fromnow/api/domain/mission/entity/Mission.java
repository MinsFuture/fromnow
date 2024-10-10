package com.knu.fromnow.api.domain.mission.entity;

import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "missions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Mission extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    private int opportunity;

    private boolean missionComplete;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Mission(int opportunity, boolean missionComplete, Member member) {
        this.opportunity = opportunity;
        this.missionComplete = missionComplete;
        this.member = member;
    }
}
