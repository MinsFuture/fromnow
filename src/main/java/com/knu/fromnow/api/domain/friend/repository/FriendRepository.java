package com.knu.fromnow.api.domain.friend.repository;

import com.knu.fromnow.api.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
}
