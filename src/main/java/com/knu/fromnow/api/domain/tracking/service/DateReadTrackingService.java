package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DateReadTrackingService {
    private final DateReadTrackingRepository dateReadTrackingRepository;

    public void initDateReadTracking(Diary diary, Member member, LocalDate today){
        DateReadTracking dateReadTracking = DateReadTracking.builder()
                .diaryId(diary.getId())
                .memberId(member.getId())
                .date(today)
                .isWrite(false)
                .lastedMemberReadTime(today.atStartOfDay())
                .build();

        dateReadTrackingRepository.save(dateReadTracking);
    }
}
