package com.knu.fromnow.api.domain.friend.dto.request;

import lombok.Getter;

@Getter
public class SentFriendDto {
    // 내가 요청을 보낼 사람의 프로필 이름
    private String sentProfileName;
}
