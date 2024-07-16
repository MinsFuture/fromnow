package com.knu.fromnow.api.domain.diary.repository;

import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, Long> {
}
