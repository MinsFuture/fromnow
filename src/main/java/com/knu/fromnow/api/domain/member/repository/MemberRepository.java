package com.knu.fromnow.api.domain.member.repository;

import com.knu.fromnow.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdentifier(String identifier);
    Optional<Member> findByProfileName(String profileName);
    boolean existsByProfileName(String profileName);
    boolean existsByIdentifier(String identifier);
}
