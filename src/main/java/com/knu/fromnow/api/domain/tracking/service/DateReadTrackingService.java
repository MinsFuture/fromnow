package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.global.error.custom.DateReadTrackingException;
import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
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

    public void boardCreateUpdate(Member me, Diary diary, LocalDateTime now){
        DateReadTracking dateReadTracking = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(me.getId(), diary.getId(), now.toLocalDate())
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));
        dateReadTracking.updateIsWrite();
        dateReadTracking.updateLastedMemberReadTime(now);
        dateReadTrackingRepository.save(dateReadTracking);
    }

}
