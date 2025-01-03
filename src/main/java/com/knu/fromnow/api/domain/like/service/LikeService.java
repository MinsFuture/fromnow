package com.knu.fromnow.api.domain.like.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardCustomRepository;
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
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final BoardCustomRepository boardCustomRepository;
    private final ValidationService validationService;

    public ApiDataResponse<List<BoardOverViewResponseDto>> getAllMyLikeBoards(PrincipalDetails principalDetails) {
        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        return ApiDataResponse.successResponse("내가 좋아요 누른 게시글들 불러오기 성공!", boardCustomRepository.findBoardsLikedByMember(me.getId()));

    }
}
