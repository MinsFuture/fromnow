package com.knu.fromnow.api.domain.tracking.service;

import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DateReadTrackingService {
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DiaryMemberRepository diaryMemberRepository;

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

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    @Transactional
    public void initDateReadTrackingAtMidNight() {
        // Step 1: 초대가 수락된 모든 DiaryMember 정보 조회
        List<DiaryMember> diaryMembers = diaryMemberRepository.findByAcceptedInviteTrue();

        // Step 2: DateReadTracking 리스트 생성
        List<DateReadTracking> trackings = new ArrayList<>();

        // Step 3: 각 멤버의 다이어리에 대해 DateReadTracking 생성
        for (DiaryMember diaryMember : diaryMembers) {
            DateReadTracking tracking = DateReadTracking.builder()
                    .memberId(diaryMember.getMember().getId())
                    .diaryId(diaryMember.getDiary().getId())
                    .date(LocalDate.now()) // 오늘 날짜로 설정
                    .isWrite(false) // 초기값 false
                    .lastedMemberReadTime(LocalDate.now().atStartOfDay()) // 자정 시간 설정
                    .build();

            // 리스트에 추가
            trackings.add(tracking);
        }

        // Step 4: 한꺼번에 저장
        dateReadTrackingRepository.saveAll(trackings);
    }
}
