package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.board.repository.BoardRepository;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
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
import com.knu.fromnow.api.global.error.custom.MemberException;
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

    public ApiDiaryResponse<List<BoardOverViewResponseDto>> getBoardOverviews(int page, int size, Long diaryId){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdTime").descending());
        Page<Board> boards = boardRepository.findByDiaryId(diaryId, pageRequest);

        List<Board> contents = boards.getContent();
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

        return ApiDiaryResponse.<List<BoardOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message("글 불러오기 성공!")
                .data(boardOverViewResponseDtos)
                .build();
    }
}
