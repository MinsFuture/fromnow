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

    /**
     * 홈 화면에 띄워줄 내 다이어리 리스트 + 해당 다이어리에 속해있는 멤버들의 프로필 사진들을 반환하는 로직
     * @param memberId
     *
     * @return DiaryOverViewResponseDto
     */
    @Override
    public List<DiaryOverViewResponseDto> fetchDiaryOverViewDtosByMe(Long memberId) {
        QDiary diary = QDiary.diary;
        QDiaryMember diaryMember = QDiaryMember.diaryMember;
        QMember member = QMember.member;

        List<Tuple> results = jpaQueryFactory
                .select(diary, diaryMember, member)
                .from(diaryMember)
                .join(diaryMember.diary, diary).fetchJoin()
                .join(diaryMember.member, member).fetchJoin()
                .where(diaryMember.member.id.eq(memberId))
                .fetch();

        Map<Long, DiaryOverViewResponseDto> diaryMap = new LinkedHashMap<>();
        for (Tuple tuple : results) {
            var diaryEntity = tuple.get(diary);
            var receivedAt = tuple.get(diaryMember.recievedAt);
            var photoUrl = tuple.get(member.photoUrl);

            diaryMap.computeIfAbsent(diaryEntity.getId(), id ->
                    DiaryOverViewResponseDto.builder()
                            .id(diaryEntity.getId())
                            .title(diaryEntity.getTitle())
                            .photoUrls(new ArrayList<>())
                            .createdAt(diaryEntity.getCreatedAt().toString())
                            .recivedAt(receivedAt != null ? receivedAt.toString() : diaryEntity.getCreatedAt().toString()) // Null 처리
                            .build()
            ).getPhotoUrls().add(photoUrl);
        }

        return new ArrayList<>(diaryMap.values());
    }
}
