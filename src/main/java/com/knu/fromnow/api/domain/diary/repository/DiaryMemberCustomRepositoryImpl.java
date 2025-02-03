package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.entity.QDiary;
import com.knu.fromnow.api.domain.diary.entity.QDiaryMember;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DiaryMemberCustomRepositoryImpl implements DiaryMemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> getUnacceptedDiaryIdsByMember(Member member) {

        QDiaryMember diaryMember = QDiaryMember.diaryMember;

        return jpaQueryFactory.select(diaryMember.diary.id)
                .from(diaryMember)
                .where(diaryMember.member.eq(member),
                        diaryMember.acceptedInvite.isFalse())
                .fetch();
    }

    @Override
    public List<String> fetchMemberPhotoUrlsByDiary(Diary diary) {
        QDiaryMember diaryMember = QDiaryMember.diaryMember;
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(member.photoUrl)
                .from(diaryMember)
                .join(diaryMember.member, member)
                .where(diaryMember.diary.eq(diary),
                        diaryMember.acceptedInvite.isTrue())
                .fetch();
    }

    @Override
    public Map<Member, LocalDateTime> findRecievedAtByDiaryAndMembers(Diary diary, List<Member> members) {
        QDiaryMember qDiaryMember = QDiaryMember.diaryMember;

        List<DiaryMember> diaryMembers = jpaQueryFactory
                .selectFrom(qDiaryMember)
                .where(
                        qDiaryMember.diary.eq(diary)
                                .and(qDiaryMember.member.in(members))
                )
                .fetch();

        return diaryMembers.stream()
                .collect(Collectors.toMap(DiaryMember::getMember, DiaryMember::getRecievedAt));
    }

    @Override
    public List<Member> findMembersByDiaryIdExceptMe(Diary diary, Member member) {
        QDiaryMember qDiaryMember = QDiaryMember.diaryMember;
        return jpaQueryFactory
                .select(qDiaryMember.member)
                .from(qDiaryMember)
                .where(qDiaryMember.diary.eq(diary)
                        .and(qDiaryMember.member.ne(member))
                )
                .fetch();
    }

    @Override
    public List<DiaryOverViewResponseDto> fetchDiaryOverViewDtosByMe(Member me) {
        QDiary diary = QDiary.diary;
        QMember member = QMember.member;
        QDiaryMember diaryMember = QDiaryMember.diaryMember;

        // 사용자가 속한 다이어리 목록 조회
        List<Diary> diaryList = jpaQueryFactory
                .select(diary)
                .from(diaryMember)
                .join(diaryMember.diary, diary)
                .where(diaryMember.member.eq(me),
                        diaryMember.acceptedInvite.isTrue())
                .fetch();

        // 다이어리별 멤버들의 photoUrl과 receivedAt을 조회하여 DTO 변환
        return diaryList.stream().map(d -> {
            List<String> photoUrls = jpaQueryFactory
                    .select(member.photoUrl)
                    .from(diaryMember)
                    .join(diaryMember.member, member)
                    .where(diaryMember.diary.eq(d),
                            diaryMember.acceptedInvite.isTrue())
                    .fetch();

            // receivedAt 조회
            LocalDateTime receivedAt = jpaQueryFactory
                    .select(diaryMember.recievedAt)
                    .from(diaryMember)
                    .where(diaryMember.diary.eq(d),
                            diaryMember.member.eq(me))
                    .fetchOne();

            // DTO 변환 및 반환
            return DiaryOverViewResponseDto.builder()
                    .id(d.getId())
                    .title(d.getTitle())
                    .photoUrls(photoUrls)
                    .createdAt(d.getCreatedAt().toString())
                    .recivedAt(String.valueOf(receivedAt))  // receivedAt 값 추가
                    .build();
        }).collect(Collectors.toList());
    }

}
