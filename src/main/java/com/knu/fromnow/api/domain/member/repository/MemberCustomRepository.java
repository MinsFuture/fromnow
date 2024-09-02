package com.knu.fromnow.api.domain.member.repository;

import com.knu.fromnow.api.domain.member.entity.Member;

import java.util.List;

public interface MemberCustomRepository {
    List<Member> findMembersByProfileNameContainingIgnoreCase(String profileName);
}
