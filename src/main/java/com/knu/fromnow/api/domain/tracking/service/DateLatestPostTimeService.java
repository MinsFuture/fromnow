package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.global.error.custom.DateLatestPostTimeException;
import com.knu.fromnow.api.global.error.errorcode.custom.DateLatestPostTimeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DateLatestPostTimeService {
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final DiaryRepository diaryRepository;

    public void initDateLatestPostTime(Diary diary, LocalDate today){
        DateLatestPostTime dateLatestPostTime = DateLatestPostTime.builder()
                .diaryId(diary.getId())
                .date(today)
                .latestPostTime(today.atStartOfDay())
                .build();

        dateLatestPostTimeRepository.save(dateLatestPostTime);
    }

    public void boardCreateUpdate(Diary diary, LocalDateTime now){
        DateLatestPostTime dateLatestPostTime = dateLatestPostTimeRepository.findByDiaryIdAndDate(diary.getId(), now.toLocalDate())
                .orElseThrow(() -> new DateLatestPostTimeException(DateLatestPostTimeErrorCode.NO_DATE_LATEST_POST_TIME_EXCEPTION));
        dateLatestPostTime.updateLatestPostTime(now);
        dateLatestPostTimeRepository.save(dateLatestPostTime);
    }



}
