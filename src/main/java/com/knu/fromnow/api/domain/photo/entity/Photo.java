package com.knu.fromnow.api.domain.photo.entity;

import com.knu.fromnow.api.domain.diary.entity.Diary;
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

@Entity(name = "photos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Photo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    private LocalDateTime createdTime;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Builder
    public Photo(LocalDateTime createdTime, String photoUrl, Diary diary) {
        this.createdTime = LocalDateTime.now();
        this.photoUrl = photoUrl;
        this.diary = diary;
        diary.getPhotoList().add(this);
    }
}
