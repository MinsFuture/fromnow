package com.knu.fromnow.api.domain.board.service;

import com.knu.fromnow.api.domain.board.dto.request.BoardCreateRequestDto;
import com.knu.fromnow.api.domain.board.dto.request.DiaryChooseRequestDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardLikeResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardCreateResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.DiaryChooseResponseDto;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.like.repository.LikeRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
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

    public ApiDataResponse<BoardCreateResponseDto> createBoard(MultipartFile file, BoardCreateRequestDto boardCreateRequestDto, Long diaryId, PrincipalDetails principalDetails) {

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Board board = Board.builder()
                .content(boardCreateRequestDto.getContent())
                .diary(diary)
                .member(member)
                .build();

        boardPhotoService.uploadToBoardPhotos(file, board);

        member.getBoardList().add(board);
        boardRepository.save(board);

        return ApiDataResponse.<BoardCreateResponseDto>builder()
                .status(true)
                .code(200)
                .message("일상 등록 성공!")
                .data(BoardCreateResponseDto.fromBoard(board))
                .build();
    }

    public ApiDataResponse<List<BoardOverViewResponseDto>> getBoardOverviews(Long diaryId, Long year, Long month, Long day, PrincipalDetails principalDetails) {
        LocalDate currentDate = LocalDate.of(year.intValue(), month.intValue(), day.intValue());

        // 오늘의 시작과 끝 시간
        LocalDateTime startDateTime = currentDate.atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endDateTime = currentDate.plusDays(1).atStartOfDay(); // 내일 00:00:00 (exclusive)

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Board> boards = boardRepository.findByDiaryIdAndCreatedAtBetween(diaryId, startDateTime, endDateTime);
        List<BoardOverViewResponseDto> boardOverViewResponseDtos = getBoardOverViewResponseDtos(boards, member);

        List<DiaryMember> diaryMembers = member.getDiaryMembers();
        boolean hasMatchingMember = diaryMembers.stream()
                .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

        if (!hasMatchingMember) {
            throw new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION);
        }

        return ApiDataResponse.<List<BoardOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message(year + "-" + month + "-" + day + "에 해당하는 글은 다음과 같습니다")
                .data(boardOverViewResponseDtos)
                .build();
    }

    /**
     * Pagination한 Board로 responseDto를 만드는 메서드
     *
     * @param contents
     * @return
     */
    public List<BoardOverViewResponseDto> getBoardOverViewResponseDtos(List<Board> contents, Member member) {
        List<BoardOverViewResponseDto> boardOverViewResponseDtos = new ArrayList<>();

        for (Board board : contents) {
            BoardOverViewResponseDto boardOverViewResponseDto =
                    BoardOverViewResponseDto.builder()
                            .boardId(board.getId())
                            .createdDate(board.getCreatedAt().toString())
                            .profileName(board.getMember().getProfileName())
                            .profilePhotoUrl(board.getMember().getPhotoUrl())
                            .content(board.getContent())
                            .contentPhotoUrl(board.getBoardPhoto().getPhotoUrl())
                            .build();
            boardOverViewResponseDtos.add(boardOverViewResponseDto);
        }
        return boardOverViewResponseDtos;
    }

    public ApiDataResponse<BoardLikeResponseDto> clickLike(Long id, PrincipalDetails principalDetails) {
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

        return ApiDataResponse.<BoardLikeResponseDto>builder()
                .status(true)
                .code(200)
                .message("좋아요를 눌렀습니다")
                .data(BoardLikeResponseDto.fromBoard(board))
                .build();
    }


    public ApiDataResponse<BoardLikeResponseDto> clickDisLike(Long id, PrincipalDetails principalDetails) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardErrorCode.NO_EXIST_BOARD_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        board.dislikeBoard();
        boardRepository.save(board);

        Like like = likeRepository.findByMemberAndBoard(member, board)
                .orElseThrow(() -> new RuntimeException());
        likeRepository.delete(like);

        return ApiDataResponse.<BoardLikeResponseDto>builder()
                .status(true)
                .code(200)
                .message("좋아요를 취소했습니다")
                .data(BoardLikeResponseDto.fromBoard(board))
                .build();
    }

    public ApiDataResponse<DiaryChooseResponseDto> createBoardAndChooseDiary(MultipartFile file, DiaryChooseRequestDto diaryChooseRequestDto, PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryChooseRequestDto.getDiaryIds());
        Board board = null;
        for (Diary diary : diaryList) {
            board = Board.builder()
                    .member(member)
                    .content(diaryChooseRequestDto.getContent())
                    .like(0)
                    .diary(diary)
                    .build();
            boardPhotoService.uploadToBoardPhotos(file, board);
            member.getBoardList().add(board);
            boardRepository.save(board);
        }

        return ApiDataResponse.<DiaryChooseResponseDto>builder()
                .status(true)
                .code(200)
                .message("일상 등록 성공!")
                .data(DiaryChooseResponseDto.fromBoard(board, diaryList))
                .build();
    }


}
