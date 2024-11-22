package com.knu.fromnow.api.domain.mission.entity;

import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String title;

    private String content;

    private String missionImg;

    @Builder
    public Mission(Long id, LocalDate date, String title, String content, String missionImg) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.missionImg = missionImg;
    }
}
