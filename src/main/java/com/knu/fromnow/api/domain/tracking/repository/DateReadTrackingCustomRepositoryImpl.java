package com.knu.fromnow.api.domain.tracking.repository;

import com.knu.fromnow.api.domain.diary.entity.QDiary;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.entity.QDateReadTracking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DateReadTrackingCustomRepositoryImpl implements DateReadTrackingCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<LocalDate> findDistinctDateByDiaryId(Long diaryId) {
        QDateReadTracking dateReadTracking = QDateReadTracking.dateReadTracking;

        return jpaQueryFactory
                .select(dateReadTracking.date)  // date 필드만 선택
                .distinct()                     // 중복 제거
                .from(dateReadTracking)
                .where(dateReadTracking.diaryId.eq(diaryId))  // diaryId로 필터링// 날짜 오름차순 정렬
                .fetch();  // 결과를 리스트로 반환
    }
}
