package com.knu.fromnow.api.domain.friend.repository;

import com.knu.fromnow.api.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
