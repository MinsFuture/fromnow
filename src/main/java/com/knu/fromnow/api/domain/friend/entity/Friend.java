package com.knu.fromnow.api.domain.friend.entity;

import com.knu.fromnow.api.global.spec.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "friends")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Friend extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    private Long toMemberId;

    private Long fromMemberId;

    private String fromMemberProfileUrl;

    private boolean areWeFriend;

    @Builder
    public Friend(Long id, Long toMemberId, Long fromMemberId, String fromMemberProfileUrl, boolean areWeFriend) {
        this.id = id;
        this.toMemberId = toMemberId;
        this.fromMemberId = fromMemberId;
        this.fromMemberProfileUrl = fromMemberProfileUrl;
        this.areWeFriend = areWeFriend;
    }

    public void acceptFriend(){
        this.areWeFriend = true;
    }
}
