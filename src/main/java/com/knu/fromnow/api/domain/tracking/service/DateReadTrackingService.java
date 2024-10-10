package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DateReadTrackingService {
    private final DateReadTrackingRepository dateReadTrackingRepository;

    public void initDateReadTracking(Diary diary, Member member, LocalDateTime now){
        DateReadTracking dateReadTracking = DateReadTracking.builder()
                .diaryId(diary.getId())
                .memberId(member.getId())
                .date(now.toLocalDate())
                .isWrite(false)
                .lastedMemberReadTime(now)
                .build();

        dateReadTrackingRepository.save(dateReadTracking);
    }
}
