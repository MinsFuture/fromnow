package com.knu.fromnow.api.domain.tracking.repository;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DateReadTrackingRepository extends JpaRepository<DateReadTracking, Long> {
    Optional<DateReadTracking> findByMemberIdAndDiaryIdAndDate(Long memberId, Long diaryId, LocalDate date);
    List<DateReadTracking> findByMemberIdAndDiaryIdAndDateBetweenOrderByDateAsc(Long memberId, Long diaryId, LocalDate startDate, LocalDate endDate);
    void deleteAllByDiaryId(Long diaryId);
}
