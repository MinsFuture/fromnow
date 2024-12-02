package com.knu.fromnow.api.domain.like.repository;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberAndBoard(Member member, Board board);
    boolean existsByMemberAndBoard(Member member, Board board);
    List<Like> findByMemberAndBoardIdIn(Member member, List<Long> boardIds);
}
