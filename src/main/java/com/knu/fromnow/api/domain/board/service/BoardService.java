package com.knu.fromnow.api.domain.board.service;

import com.knu.fromnow.api.domain.board.dto.request.BoardCreateRequestDto;
import com.knu.fromnow.api.domain.board.dto.request.DiaryChooseRequestDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardLikeResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardCreateResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.DiaryChooseResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.TodayBoardResponseDto;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.like.repository.LikeRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.global.error.custom.BoardException;
import com.knu.fromnow.api.global.error.custom.DateLatestPostTimeException;
import com.knu.fromnow.api.global.error.custom.DateReadTrackingException;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.LikeException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.BoardErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DateLatestPostTimeErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DateReadTrackingErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.LikeErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardPhotoService boardPhotoService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final LikeRepository likeRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final FirebaseService firebaseService;
    private final DiaryMemberRepository diaryMemberRepository;

    public ApiDataResponse<BoardCreateResponseDto> createBoard(MultipartFile file, BoardCreateRequestDto boardCreateRequestDto, Long diaryId, PrincipalDetails principalDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));
        List<Long> memberIds = diaryMemberRepository.findMemberIdsByDiaryId(diaryId);
        List<Member> members = memberRepository.findByIdIn(memberIds);
        members.removeIf(member -> member.getId().equals(me.getId()));

        Board board = Board.builder()
                .content(boardCreateRequestDto.getContent())
                .diary(diary)
                .member(me)
                .build();

        boardPhotoService.uploadToBoardPhotos(file, board);
        me.getBoardList().add(board);
        boardRepository.save(board);

        // DateReadTracking 및 DateLatestPostTime 엔티티 업데이트
        DateReadTracking dateReadTracking = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(me.getId(), diaryId, today)
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));
        dateReadTracking.updateIsWrite();
        dateReadTracking.updateLastedMemberReadTime(now);
        dateReadTrackingRepository.save(dateReadTracking);

        DateLatestPostTime dateLatestPostTime = dateLatestPostTimeRepository.findByDiaryIdAndDate(diaryId, today)
                .orElseThrow(() -> new DateLatestPostTimeException(DateLatestPostTimeErrorCode.NO_DATE_LATEST_POST_TIME_EXCEPTION));
        dateLatestPostTime.updateLatestPostTime(now);
        dateLatestPostTimeRepository.save(dateLatestPostTime);

        List<MemberNotificationStatusDto> memberNotificationStatusDtos = firebaseService.sendNewBoardNotificationToDiaryMember(me, members, diary);

        return ApiDataResponse.<BoardCreateResponseDto>builder()
                .status(true)
                .code(200)
                .message("일상 등록 성공!")
                .data(BoardCreateResponseDto.makeFrom(board, memberNotificationStatusDtos))
                .build();
    }

    public ApiDataResponse<TodayBoardResponseDto> getTodayBoards(Long diaryId, String date, PrincipalDetails principalDetails) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(date, formatter);

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

        // 내가 마지막으로 읽은 시점과 글 씀 여부 확인
        DateReadTracking dateReadTracking = dateReadTrackingRepository.findByMemberIdAndDiaryIdAndDate(member.getId(), diaryId, currentDate)
                .orElseThrow(() -> new DateReadTrackingException(DateReadTrackingErrorCode.NO_DATE_READ_TRACKING_EXIST_EXCEPTION));
        LocalDateTime lastedMemberReadTime = dateReadTracking.getLastedMemberReadTime();
        boolean isBlur = !dateReadTracking.isWrite();

        // 해당 날짜의 다이어리에 마지막으로 글이 써진 시점
        DateLatestPostTime dateLatestPostTime = dateLatestPostTimeRepository.findByDiaryIdAndDate(diaryId, currentDate)
                .orElseThrow(() -> new DateLatestPostTimeException(DateLatestPostTimeErrorCode.NO_DATE_LATEST_POST_TIME_EXCEPTION));
        LocalDateTime lastedPostTime = dateLatestPostTime.getLatestPostTime();

        // 글이 있고, 읽은 적이 없거나 , 읽은 후에 새로운 글이 올라 온 경우
        boolean isRead = (lastedPostTime.isAfter(lastedMemberReadTime) || lastedPostTime.equals(lastedMemberReadTime));

        if (!hasMatchingMember) {
            throw new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION);
        }

        return ApiDataResponse.<TodayBoardResponseDto>builder()
                .status(true)
                .code(200)
                .message(date + " 에 해당하는 글은 다음과 같습니다")
                .data(TodayBoardResponseDto.builder()
                        .isRead(isRead)
                        .isBlur(isBlur)
                        .boardOverViewResponseDtoList(boardOverViewResponseDtos)
                        .build())
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

        List<Long> boardIds = contents.stream()
                .map(Board::getId)
                .toList();

        List<Like> likes = likeRepository.findByMemberAndBoardIdIn(member, boardIds);
        Map<Long, Like> likeMap = likes.stream()
                .collect(Collectors.toMap(like -> like.getBoard().getId(), Function.identity()));

        for (Board board : contents) {
            boolean isLiked = false;

            // 해당 게시물에 대한 좋아요 여부 확인
            Like like = likeMap.get(board.getId());
            if (like != null && like.isLiked()) {
                isLiked = true;
            }

            BoardOverViewResponseDto boardOverViewResponseDto =
                    BoardOverViewResponseDto.builder()
                            .boardId(board.getId())
                            .createdDate(board.getCreatedAt().toString())
                            .profileName(board.getMember().getProfileName())
                            .profilePhotoUrl(board.getMember().getPhotoUrl())
                            .content(board.getContent())
                            .contentPhotoUrl(board.getBoardPhoto().getPhotoUrl())
                            .likes(board.getLike())
                            .isLiked(isLiked)
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
                .isLiked(true)
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
                .orElseThrow(() -> new LikeException(LikeErrorCode.NEVER_CLICK_THE_LIKE_BUTTON_EXCEPTION));
        like.disLike();
        likeRepository.save(like);

        return ApiDataResponse.<BoardLikeResponseDto>builder()
                .status(true)
                .code(200)
                .message("좋아요를 취소했습니다")
                .data(BoardLikeResponseDto.fromBoard(board))
                .build();
    }

    public ApiDataResponse<DiaryChooseResponseDto> createBoardAndChooseDiary(MultipartFile file, DiaryChooseRequestDto diaryChooseRequestDto, PrincipalDetails principalDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryChooseRequestDto.getDiaryIds());
        List<Long> diaryIds = diaryList.stream().map(Diary::getId).toList();

        List<Board> boardList = new ArrayList<>();
        for (Diary diary : diaryList) {
            Board board = Board.builder()
                    .member(member)
                    .content(diaryChooseRequestDto.getContent())
                    .like(0)
                    .diary(diary)
                    .build();
            boardList.add(board);
            boardPhotoService.uploadToBoardPhotos(file, board);  // 파일 업로드
        }

        // Batch Insert 처리
        boardRepository.saveAll(boardList);

        // DateReadTracking 및 DateLatestPostTime 엔티티 업데이트
        List<DateReadTracking> dateReadTrackingList = dateReadTrackingRepository.findByMemberIdAndDiaryIdInAndDate(member.getId(), diaryIds, today);
        List<DateLatestPostTime> dateLatestPostTimeList = dateLatestPostTimeRepository.findByDiaryIdInAndDate(diaryIds, today);

        // DateReadTracking 업데이트
        dateReadTrackingList.forEach(DateReadTracking::updateIsWrite);
        dateReadTrackingRepository.saveAll(dateReadTrackingList);

        // DateLatestPostTime 업데이트
        dateLatestPostTimeList.forEach(dateLatestPostTime -> dateLatestPostTime.updateLatestPostTime(now));
        dateLatestPostTimeRepository.saveAll(dateLatestPostTimeList);

        return ApiDataResponse.<DiaryChooseResponseDto>builder()
                .status(true)
                .code(200)
                .message("일상 등록 성공!")
                .data(DiaryChooseResponseDto.fromBoard(boardList.get(0), diaryList))
                .build();
    }


}
