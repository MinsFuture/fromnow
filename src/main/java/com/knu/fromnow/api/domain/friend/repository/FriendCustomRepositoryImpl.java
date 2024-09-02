package com.knu.fromnow.api.domain.friend.repository;

import com.knu.fromnow.api.domain.friend.entity.QFriend;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendCustomRepositoryImpl implements FriendCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findAllRequestReceived(Long memberId){
        QFriend friend = QFriend.friend;

        return jpaQueryFactory
                .select(friend.toMemberId)
                .from(friend)
                .where(friend.fromMemberId.eq(memberId),
                        friend.areWeFriend.isFalse())
                .fetch();

    }

    @Override
    public List<Long> findAllFriendsWithEachOther(Long memberId) {
        QFriend friend = QFriend.friend;
        QFriend selfFriend = new QFriend("selfFriend");

        return jpaQueryFactory
                .select(friend.toMemberId) // toMemberId만 선택
                .from(friend)
                .join(selfFriend)
                .on(friend.toMemberId.eq(selfFriend.fromMemberId))
                .where(
                        friend.fromMemberId.eq(memberId),
                        friend.areWeFriend.isTrue(),
                        selfFriend.toMemberId.eq(memberId),
                        selfFriend.areWeFriend.isTrue()
                )
                .fetch();
    }

    @Override
    public List<Long> findFriendsAmongSpecificMembers(Long memberId, List<Long> checkMemberIds) {
        QFriend friend = QFriend.friend;
        QFriend selfFriend = new QFriend("selfFriend");

        return jpaQueryFactory
                .select(friend.toMemberId) // toMemberId만 선택
                .from(friend)
                .join(selfFriend)
                .on(friend.toMemberId.eq(selfFriend.fromMemberId))
                .where(
                        friend.fromMemberId.eq(memberId),
                        friend.areWeFriend.isTrue(),
                        selfFriend.toMemberId.eq(memberId),
                        selfFriend.areWeFriend.isTrue(),
                        friend.toMemberId.in(checkMemberIds) // 특정 멤버들 중 친구인 경우만
                )
                .fetch();
    }
}
