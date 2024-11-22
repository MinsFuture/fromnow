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
public class DateLatestPostTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_lasest_post_time_id")
    private Long id;

    private Long diaryId;

    private LocalDate date;

    private LocalDateTime latestPostTime;

    @Builder
    public DateLatestPostTime(Long id, Long diaryId, LocalDate date, LocalDateTime latestPostTime) {
        this.id = id;
        this.diaryId = diaryId;
        this.date = date;
        this.latestPostTime = latestPostTime;
    }

    public void updateLatestPostTime(LocalDateTime newPostTime) {
        if (latestPostTime == null || newPostTime.isAfter(latestPostTime)) {
            this.latestPostTime = newPostTime;
        }
    }
}
