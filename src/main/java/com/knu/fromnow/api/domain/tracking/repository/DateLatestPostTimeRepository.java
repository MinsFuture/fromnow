package com.knu.fromnow.api.domain.tracking.repository;

import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DateLatestPostTimeRepository extends JpaRepository<DateLatestPostTime, Long> {
    List<DateLatestPostTime> findByDiaryIdAndDateBetween(Long diaryId, LocalDate startDate, LocalDate endDate);
    void deleteAllByDiaryId(Long diaryId);
}
