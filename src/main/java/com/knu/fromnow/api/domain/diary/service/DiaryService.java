package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.ApiDiaryResponse;
import com.knu.fromnow.api.domain.diary.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.entity.Photo;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;

    public ApiBasicResponse createDiary(CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails){

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = Diary.builder()
                .title(createDiaryDto.getTitle())
                .diaryType(createDiaryDto.getDiaryType())
                .owner(member)
                .build();

        diaryRepository.save(diary);

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .build();

        member.getDiaryMembers().add(diaryMember);
        diary.getDiaryMembers().add(diaryMember);

        diaryMemberRepository.save(diaryMember);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("Diary 생성 성공!")
                .build();
    }

    public ApiDiaryResponse<List<DiaryOverViewResponseDto>> getDiaryOverView(PrincipalDetails principalDetails){
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> diaryIds = diaryMemberRepository.findDiaryIdsByMemberId(member.getId());
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);

        List<DiaryOverViewResponseDto> responseDtoList = new ArrayList<>();

        for (Diary diary : diaryList) {
            DiaryOverViewResponseDto diaryOverViewResponseDto = DiaryOverViewResponseDto.builder()
                    .id(diary.getId())
                    .title(diary.getTitle())
                    .diaryType(diary.getDiaryType())
                    .build();

            responseDtoList.add(diaryOverViewResponseDto);
        }

        return ApiDiaryResponse.<List<DiaryOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message("다이어리 리스트 반환 성공!")
                .data(responseDtoList)
                .build();
    }

    public ApiDiaryResponse<List<BoardOverViewResponseDto>> getBoardOverviews(int page, int size, Long diaryId, PrincipalDetails principalDetails){
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<DiaryMember> diaryMembers = member.getDiaryMembers();
        boolean hasMatchingMember = diaryMembers.stream()
                .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

        if(!hasMatchingMember){
            throw new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION);
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdTime").descending());
        Page<Board> boards = boardRepository.findByDiaryId(diaryId, pageRequest);
        List<Board> contents = boards.getContent();

        List<BoardOverViewResponseDto> boardOverViewResponseDtos = getBoardOverViewResponseDtos(contents);

        return ApiDiaryResponse.<List<BoardOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message("글 불러오기 성공!")
                .data(boardOverViewResponseDtos)
                .build();
    }

    public ApiBasicResponse updateDiaryTitle(UpdateDiaryDto updateDiaryDto, PrincipalDetails principalDetails, Long diaryId){
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        if(!diary.getOwner().equals(member)){
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diary.updateDiaryTitle(updateDiaryDto);

        diaryRepository.save(diary);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("다이어리 이름 업데이트 성공! newTitle : " + updateDiaryDto.getNewTitle())
                .build();
    }

    public ApiBasicResponse deleteDiary(PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        if(!diary.getOwner().equals(member)){
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diaryRepository.delete(diary);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("다이어리 삭제 성공")
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
