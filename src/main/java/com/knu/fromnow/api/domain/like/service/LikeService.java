package com.knu.fromnow.api.domain.like.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.board.service.BoardService;
import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.like.repository.LikeCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeCustomRepository likeCustomRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public ApiDataResponse<List<BoardOverViewResponseDto>> getAllMyLikeBoards(PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> boardIds = likeCustomRepository.findAllBoardsWithMyLikes(member.getId());
        List<Board> boards = boardRepository.findByIdIn(boardIds);

        List<BoardOverViewResponseDto> responseDtos = boardService.getBoardOverViewResponseDtos(boards, member);

        return ApiDataResponse.<List<BoardOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .data(responseDtos)
                .build();
    }
}
