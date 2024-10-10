package com.knu.fromnow.api.domain.tracking.entity;

import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DateReadTracking extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_read_tracking_id")
    private Long id;

    private Long memberId;

    private Long diaryId;

    private LocalDate date;

    private boolean isWrite;

    private LocalDateTime lastedMemberReadTime;

    @Builder
    public DateReadTracking(Long id, Long memberId, Long diaryId, LocalDate date, boolean isWrite, LocalDateTime lastedMemberReadTime) {
        this.id = id;
        this.memberId = memberId;
        this.diaryId = diaryId;
        this.date = date;
        this.isWrite = isWrite;
        this.lastedMemberReadTime = lastedMemberReadTime;
    }

    public void updateLastedMemberReadTime(LocalDateTime now) {
        this.lastedMemberReadTime = now;
    }

    public void updateIsWrite(){
        this.isWrite = true;
    }
}
