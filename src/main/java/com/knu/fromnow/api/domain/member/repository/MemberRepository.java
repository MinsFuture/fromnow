package com.knu.fromnow.api.domain.member.repository;

import com.knu.fromnow.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProfileName(String profileName);
    List<Member> findByProfileNameIn(List<String> profileName);
    Optional<Member> findByRefreshToken(String refreshToken);
    List<Member> findByIdIn(List<Long> memberIds);
    boolean existsByProfileName(String profileName);

    @Query("SELECT m.fcmToken FROM members m WHERE m.fcmToken IS NOT NULL")
    List<String> findAllFcmTokens();
}
