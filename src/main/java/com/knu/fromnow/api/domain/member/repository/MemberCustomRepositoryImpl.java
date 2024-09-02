package com.knu.fromnow.api.domain.member.repository;

import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findMembersByProfileNameContainingIgnoreCase(String profileName) {
        QMember member = QMember.member;

        return jpaQueryFactory.selectFrom(member)
                .where(member.profileName.containsIgnoreCase(profileName))  // 부분 문자열 검색
                .fetch();
    }
}
