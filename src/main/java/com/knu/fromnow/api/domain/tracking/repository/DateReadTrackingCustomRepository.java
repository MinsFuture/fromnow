package com.knu.fromnow.api.domain.tracking.repository;

import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;

import java.time.LocalDate;
import java.util.List;

public interface DateReadTrackingCustomRepository {
    List<LocalDate> findDistinctDateByDiaryId(Long diaryId);
}
