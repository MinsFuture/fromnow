package com.knu.fromnow.api.domain.board.service;

import com.knu.fromnow.api.domain.board.dto.CreateBoardDto;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.response.ApiDiaryResponse;
import com.knu.fromnow.api.domain.diary.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.entity.Photo;
import com.knu.fromnow.api.domain.photo.service.PhotoService;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private final PhotoService photoService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;

    public ApiBasicResponse createBoard(MultipartFile[] files, CreateBoardDto createBoardDto, Long diaryId, PrincipalDetails principalDetails){

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Board board = Board.builder()
                .content(createBoardDto.getContent())
                .diary(diary)
                .member(member)
                .build();

        photoService.uploadPhoto(files, board);

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

        List<Board> boards = boardRepository.findByDiaryIdAndCreatedTimeBetween(diaryId, startDateTime, endDateTime);
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
    private static List<BoardOverViewResponseDto> getBoardOverViewResponseDtos(List<Board> contents) {
        List<BoardOverViewResponseDto> boardOverViewResponseDtos = new ArrayList<>();

        for (Board board : contents) {
            List<Photo> photoList = board.getPhotoList();
            List<String> photoUrls = new ArrayList<>();
            for (Photo photo : photoList) {
                photoUrls.add(photo.getPhotoUrl());
            }

            BoardOverViewResponseDto boardOverViewResponseDto =
                    BoardOverViewResponseDto.builder()
                            .createdDate(board.getCreatedTime().toString())
                            .profileName(board.getMember().getProfileName())
                            .profilePhotoUrl(board.getMember().getPhoto().getPhotoUrl())
                            .content(board.getContent())
                            .contentPhotoUrl(photoUrls)
                            .build();

            boardOverViewResponseDtos.add(boardOverViewResponseDto);
        }
        return boardOverViewResponseDtos;
    }


}
