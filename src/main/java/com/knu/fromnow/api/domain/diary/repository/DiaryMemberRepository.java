package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, Long> {
    @Query("SELECT dm.diary.id FROM Diary_Member dm WHERE dm.member.id = :memberId and dm.acceptedInvite = true")
    List<Long> findDiaryIdsByMemberIdWithAcceptedInviteTrue(@Param("memberId") Long memberId);

    @Query("SELECT dm.member.id FROM Diary_Member dm WHERE dm.diary.id = :diaryId")
    List<Long> findMemberIdsByDiaryId(@Param("diaryId") Long diaryId);

    @Query("SELECT dm.member.id FROM Diary_Member dm WHERE dm.diary.id = :diaryId and dm.acceptedInvite = true" )
    List<Long> findMemberIdsByDiaryIdAndAcceptedInviteTrue(@Param("diaryId") Long diaryId);

    @Query("SELECT dm.member.id FROM Diary_Member dm WHERE dm.diary.id = :diaryId and dm.acceptedInvite = false " )
    List<Long> findMemberIdsByDiaryIdAndAcceptedInviteFalse(@Param("diaryId") Long diaryId);

    Optional<DiaryMember> findByDiaryAndMember(Diary diary, Member member);
    Optional<DiaryMember> findByDiaryAndMemberAndAcceptedInviteFalse(Diary diary, Member member);
    List<DiaryMember> findByDiaryAndMemberIn(Diary diary, List<Member> members);
    List<DiaryMember> findByDiary(Diary diary);

    boolean existsByDiaryAndMember(Diary diary, Member member);
    List<DiaryMember> findByAcceptedInviteTrue();
}
