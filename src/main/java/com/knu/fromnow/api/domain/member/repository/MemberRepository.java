package com.knu.fromnow.api.domain.member.repository;

import com.knu.fromnow.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProfileName(String profileName);
    Optional<Member> findByRefreshToken(String refreshToken);
    List<Member> findByIdIn(List<Long> memberIds);
    boolean existsByProfileName(String profileName);
}
