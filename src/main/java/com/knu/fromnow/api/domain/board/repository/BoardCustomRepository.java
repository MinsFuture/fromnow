package com.knu.fromnow.api.domain.board.repository;

import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardCustomRepository {
   List<BoardOverViewResponseDto> findByDiaryIdAndCreatedAtBetween(Long myId, Long diaryId, LocalDateTime startDate, LocalDateTime endDate);
   List<BoardOverViewResponseDto> findBoardsLikedByMember(Long myId);
}
