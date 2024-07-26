package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, Long> {
    @Query("SELECT dm.diary.id FROM Diary_Member dm WHERE dm.member.id = :memberId")
    List<Long> findDiaryIdsByMemberId(@Param("memberId") Long memberId);
}
