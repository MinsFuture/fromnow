package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.QDiary;
import com.knu.fromnow.api.domain.diary.entity.QDiaryMember;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.knu.fromnow.api.domain.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class DiaryMemberCustomRepositoryImpl implements DiaryMemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DiaryOverViewResponseDto> fetchDiaryOverviewDtosByDiaryMembers(List<Diary> diaryList) {
        QDiary diary = QDiary.diary;
        QMember member = QMember.member;
        QDiaryMember diaryMember = QDiaryMember.diaryMember;

        // 다이어리마다 멤버들의 photoUrl을 리스트로 수집
        return diaryList.stream().map(d -> {
            List<String> photoUrls = jpaQueryFactory
                    .select(member.photoUrl)
                    .from(diaryMember)
                    .join(diaryMember.member, member)
                    .where(diaryMember.diary.eq(d))
                    .fetch();

            // DiaryOverViewResponseDto로 변환
            return DiaryOverViewResponseDto.builder()
                    .id(d.getId())
                    .title(d.getTitle())
                    .photoUrls(photoUrls)
                    .build();
        }).collect(Collectors.toList());
    }


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
                .where(diaryMember.diary.eq(diary))
                .fetch();
    }
}
