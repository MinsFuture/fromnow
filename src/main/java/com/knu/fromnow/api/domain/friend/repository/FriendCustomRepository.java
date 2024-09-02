package com.knu.fromnow.api.domain.friend.repository;

import com.knu.fromnow.api.domain.friend.entity.Friend;

import java.util.List;

public interface FriendCustomRepository {

    List<Long> findAllFriendsWithEachOther(Long memberId);
    List<Long> findFriendsAmongSpecificMembers(Long memberId, List<Long> checkMemberIds);
    List<Long> findAllRequestReceived(Long memberId);
}
