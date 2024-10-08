package com.knu.fromnow.api.domain.read.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.read.dto.BoardReadResponseDto;
import com.knu.fromnow.api.domain.read.entity.BoardRead;
import com.knu.fromnow.api.domain.read.repository.BoardReadRepository;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardReadService {

    private final BoardReadRepository boardReadRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public ApiDataResponse<BoardReadResponseDto> readBoard(Long id, PrincipalDetails principalDetails) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardErrorCode.NO_EXIST_BOARD_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        BoardRead boardRead = BoardRead.builder()
                .board(board)
                .member(member)
                .readAt(LocalDateTime.now())
                .build();

        boardReadRepository.save(boardRead);
        board.getBoardReadList().add(boardRead);
        member.getBoardReadList().add(boardRead);

        return ApiDataResponse.<BoardReadResponseDto>builder()
                .status(true)
                .code(200)
                .message("해당 글과 읽은 유저는 다음과 같습니다")
                .data(BoardReadResponseDto.fromBoardRead(boardRead))
                .build();
    }
}
