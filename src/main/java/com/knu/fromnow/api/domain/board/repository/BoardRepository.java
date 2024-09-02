package com.knu.fromnow.api.domain.board.repository;

import com.knu.fromnow.api.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByDiaryIdAndCreatedTimeBetween(Long diaryId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
