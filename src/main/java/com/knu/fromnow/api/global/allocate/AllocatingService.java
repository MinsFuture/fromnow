package com.knu.fromnow.api.global.allocate;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
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
public class AllocatingService {

    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberRepository diaryMemberRepository;

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

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    @Transactional
    public void initDateLatestPostTimeAtMidNight(){
        List<Diary> diaries = diaryRepository.findAll();

        List<DateLatestPostTime> trackings = new ArrayList<>();

        for (Diary diary : diaries) {
            DateLatestPostTime dateLatestPostTime = DateLatestPostTime.builder()
                    .diaryId(diary.getId())
                    .date(LocalDate.now())
                    .latestPostTime(LocalDate.now().atStartOfDay())
                    .build();

            trackings.add(dateLatestPostTime);
        }

        dateLatestPostTimeRepository.saveAll(trackings);
    }


}
