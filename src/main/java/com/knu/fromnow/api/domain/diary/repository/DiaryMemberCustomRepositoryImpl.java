package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.QDiaryMember;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Member> getMemberListsByInDiary(Diary diary){

        QDiaryMember diaryMember = QDiaryMember.diaryMember;

        return jpaQueryFactory.select(diaryMember.member)
                .from(diaryMember)
                .where(diaryMember.diary.eq(diary))
                .fetch();
    }
}
