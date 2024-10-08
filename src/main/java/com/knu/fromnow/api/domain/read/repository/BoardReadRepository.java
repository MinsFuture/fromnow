package com.knu.fromnow.api.domain.read.repository;


import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.read.entity.BoardRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardReadRepository extends JpaRepository<BoardRead, Long> {
    List<BoardRead> findByMemberAndBoardIn(Member member, List<Board> boards);
}
