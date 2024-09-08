package com.knu.fromnow.api.domain.like.repository;

import com.knu.fromnow.api.domain.like.entity.QLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findAllBoardsWithMyLikes(Long memberId){
        QLike like = QLike.like;

        return jpaQueryFactory
                .select(like.board.id)
                .from(like)
                .where(like.member.id.eq(memberId))
                .fetch();
    }
}
