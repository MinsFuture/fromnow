package com.knu.fromnow.api.domain.board.service;

import com.knu.fromnow.api.domain.board.dto.CreateBoardDto;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.response.ApiDiaryResponse;
import com.knu.fromnow.api.domain.diary.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.like.repository.LikeRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardPhotoService boardPhotoService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final LikeRepository likeRepository;

    public ApiBasicResponse createBoard(MultipartFile file, CreateBoardDto createBoardDto, Long diaryId, PrincipalDetails principalDetails){

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Board board = Board.builder()
                .content(createBoardDto.getContent())
                .diary(diary)
                .member(member)
                .build();

        boardPhotoService.uploadToBoardPhotos(file, board);

        member.getBoardList().add(board);
        boardRepository.save(board);

        ApiBasicResponse apiBasicResponse = ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("일기 생성 성공!")
                .build();

        return apiBasicResponse;
    }

    public ApiDiaryResponse<List<BoardOverViewResponseDto>> getBoardOverviews(Long diaryId, LocalDate date, PrincipalDetails principalDetails){
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.plusDays(1).atStartOfDay();

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Board> boards = boardRepository.findByDiaryIdAndCreatedAtBetween(diaryId, startDateTime, endDateTime);
        List<BoardOverViewResponseDto> boardOverViewResponseDtos = getBoardOverViewResponseDtos(boards);

        List<DiaryMember> diaryMembers = member.getDiaryMembers();
        boolean hasMatchingMember = diaryMembers.stream()
                .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

        if(!hasMatchingMember){
            throw new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION);
        }

        return ApiDiaryResponse.<List<BoardOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message(date + "에 해당하는 글 불러오기 성공!")
                .data(boardOverViewResponseDtos)
                .build();
    }

    /**
     * Pagination한 Board로 responseDto를 만드는 메서드
     * @param contents
     * @return
     */
    public List<BoardOverViewResponseDto> getBoardOverViewResponseDtos(List<Board> contents) {
        List<BoardOverViewResponseDto> boardOverViewResponseDtos = new ArrayList<>();

        for (Board board : contents) {
            List<BoardPhoto> photoList = board.getPhotoList();
            List<String> photoUrls = new ArrayList<>();
            for (BoardPhoto photo : photoList) {
                photoUrls.add(photo.getPhotoUrl());
            }

            BoardOverViewResponseDto boardOverViewResponseDto =
                    BoardOverViewResponseDto.builder()
                            .boardId(board.getId())
                            .createdDate(board.getCreatedAt().toString())
                            .profileName(board.getMember().getProfileName())
                            .profilePhotoUrl(board.getMember().getPhotoUrl())
                            .content(board.getContent())
                            .contentPhotoUrl(photoUrls)
                            .build();

            boardOverViewResponseDtos.add(boardOverViewResponseDto);
        }
        return boardOverViewResponseDtos;
    }

    public ApiBasicResponse clickLike(Long id, PrincipalDetails principalDetails){
        // 추후 내가 누른 좋아요 볼 수 있도록 로직 추가
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardErrorCode.NO_EXIST_BOARD_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        board.likeBoard();
        boardRepository.save(board);

        Like like = Like.builder()
                .board(board)
                .member(member)
                .build();
        likeRepository.save(like);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("좋아요를 눌렀습니다!")
                .build();
    }


    public ApiBasicResponse clickDisLike(Long id, PrincipalDetails principalDetails) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardErrorCode.NO_EXIST_BOARD_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        board.dislikeBoard();
        boardRepository.save(board);

        Like like = likeRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new RuntimeException());
        likeRepository.delete(like);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("좋아요를 취소했습니다!")
                .build();
    }
}
