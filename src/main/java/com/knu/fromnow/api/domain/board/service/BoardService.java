package com.knu.fromnow.api.domain.board.service;

import com.knu.fromnow.api.domain.board.dto.request.BoardCreateRequestDto;
import com.knu.fromnow.api.domain.board.dto.request.DiaryChooseRequestDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardLikeResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardCreateResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.DiaryChooseResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.TodayBoardResponseDto;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardCustomRepository;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.like.entity.Like;
import com.knu.fromnow.api.domain.like.repository.LikeRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.domain.tracking.entity.DateLatestPostTime;
import com.knu.fromnow.api.domain.tracking.entity.DateReadTracking;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.tracking.service.DateLatestPostTimeService;
import com.knu.fromnow.api.domain.tracking.service.DateReadTrackingService;
import com.knu.fromnow.api.global.error.custom.LikeException;
import com.knu.fromnow.api.global.error.errorcode.custom.LikeErrorCode;
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    private final ValidationService validationService;
    private final BoardPhotoService boardPhotoService;
    private final BoardRepository boardRepository;
    private final DiaryRepository diaryRepository;
    private final LikeRepository likeRepository;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;
    private final FirebaseService firebaseService;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final DateReadTrackingService dateReadTrackingService;
    private final DateLatestPostTimeService dateLatestPostTimeService;
    private final BoardCustomRepository boardCustomRepository;

    public ApiDataResponse<BoardCreateResponseDto> createBoard(MultipartFile file, BoardCreateRequestDto boardCreateRequestDto, Long diaryId, PrincipalDetails principalDetails) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = validationService.validateDiaryById(diaryId);

        List<Member> members = diaryMemberCustomRepository.findMembersByDiaryIdExceptMe(diary, me);
        Board board = Board.create(boardCreateRequestDto.getContent());
        // 연관관계 설정
        diary.addBoard(board);
        me.addBoard(board);
        boardPhotoService.uploadToBoardPhotos(file, board);
        boardRepository.save(board);

        // DateReadTracking 및 DateLatestPostTime 엔티티 업데이트
        dateReadTrackingService.boardCreateUpdate(me, diary, now);
        dateLatestPostTimeService.boardCreateUpdate(diary, now);

        List<MemberNotificationStatusDto> memberNotificationStatusDtos = firebaseService.sendNewBoardNotificationToDiaryMember(me, members, diary);

        return ApiDataResponse.successResponse("일상 등록 성공", BoardCreateResponseDto.of(board, memberNotificationStatusDtos));
    }

    public ApiDataResponse<TodayBoardResponseDto> getTodayBoards(Long diaryId, String date, PrincipalDetails principalDetails) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse(date, formatter);

        // 오늘의 시작과 끝 시간
        LocalDateTime startDateTime = currentDate.atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endDateTime = currentDate.plusDays(1).atStartOfDay(); // 내일 00:00:00 (exclusive)

        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        List<BoardOverViewResponseDto> boardOverViewResponseDtos
                = boardCustomRepository.findByDiaryIdAndCreatedAtBetween(me.getId(), diaryId, startDateTime, endDateTime);

        // 내가 마지막으로 읽은 시점과 글 씀 여부 확인
        DateReadTracking dateReadTracking = validationService.validateDateReadTracking(me.getId(), diaryId, currentDate);
        LocalDateTime lastedMemberReadTime = dateReadTracking.getLastedMemberReadTime();
        boolean isBlur = !dateReadTracking.isWrite();

        // 해당 날짜의 다이어리에 마지막으로 글이 써진 시점
        DateLatestPostTime dateLatestPostTime = validationService.validateDateLatestPostTime(diaryId, currentDate);
        LocalDateTime lastedPostTime = dateLatestPostTime.getLatestPostTime();
        boolean isRead = (lastedPostTime.isAfter(lastedMemberReadTime) || lastedPostTime.equals(lastedMemberReadTime));

        return ApiDataResponse.successResponse(date + " 에 해당하는 글은 다음과 같습니다", TodayBoardResponseDto.of(isBlur, isRead, boardOverViewResponseDtos));
    }

    public ApiDataResponse<BoardLikeResponseDto> clickLike(Long id, PrincipalDetails principalDetails) {
        Board board = validationService.validateBoardById(id);
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());

        if(likeRepository.existsByMemberAndBoard(member, board)){
            throw new LikeException(LikeErrorCode.ALREADY_CLICK_LIKE_EXCEPTION);
        }

        board.likeBoard();
        boardRepository.save(board);

        Like like = Like.initLike(board, member);
        likeRepository.save(like);

        return ApiDataResponse.successResponse("좋아요를 눌렀습니다", BoardLikeResponseDto.from(board));
    }


    public ApiDataResponse<BoardLikeResponseDto> clickDisLike(Long id, PrincipalDetails principalDetails) {
        Board board = validationService.validateBoardById(id);
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());

        board.dislikeBoard();
        boardRepository.save(board);

        Like like = validationService.validateLikeByMemberAndBoard(member, board);
        likeRepository.delete(like);

        return ApiDataResponse.successResponse("좋아요를 취소했습니다", BoardLikeResponseDto.from(board));
    }

    public ApiDataResponse<DiaryChooseResponseDto> createBoardAndChooseDiary(MultipartFile file, DiaryChooseRequestDto diaryChooseRequestDto, PrincipalDetails principalDetails) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        log.info("ids : {}", diaryChooseRequestDto.getDiaryIds());
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
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

        return ApiDataResponse.successResponse("일상 등록 성공!", DiaryChooseResponseDto.of(boardList.get(0), diaryList));
    }


}
