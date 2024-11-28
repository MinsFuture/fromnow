package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

}
