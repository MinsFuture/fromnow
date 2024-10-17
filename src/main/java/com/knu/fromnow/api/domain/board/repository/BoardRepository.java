package com.knu.fromnow.api.domain.board.repository;

import com.knu.fromnow.api.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM boards b JOIN FETCH b.boardPhoto WHERE b.diary.id = :diaryId AND b.createdAt BETWEEN :startDate AND :endDate")
    List<Board> findByDiaryIdAndCreatedAtBetween(
            @Param("diaryId") Long diaryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Board> findByIdIn(List<Long> boardIds);

}
