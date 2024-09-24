package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final DiaryMemberService diaryMemberService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;

    public ApiDataResponse<DiaryCreateResponseDto> createDiary(CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails) {

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = Diary.builder()
                .title(createDiaryDto.getTitle())
                .owner(member)
                .build();

        diaryRepository.save(diary);

        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .acceptedInvite(true)
                .build();

        member.getDiaryMembers().add(diaryMember);
        diary.getDiaryMembers().add(diaryMember);

        diaryMemberRepository.save(diaryMember);

        return ApiDataResponse.<DiaryCreateResponseDto>builder()
                .status(true)
                .code(200)
                .message("다이어리 생성 성공")
                .data(DiaryCreateResponseDto.fromDiary(diary))
                .build();
    }

    public ApiDataResponse<List<DiaryOverViewResponseDto>> getDiaryOverView(PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> diaryIds = diaryMemberRepository.findDiaryIdsByMemberId(member.getId());
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);

        List<DiaryOverViewResponseDto> responseDtoList = diaryMemberCustomRepository.fetchDiaryOverviewDtosByDiaryMembers(diaryList);

        return ApiDataResponse.<List<DiaryOverViewResponseDto>>builder()
                .status(true)
                .code(200)
                .message("다이어리 리스트 반환 성공!")
                .data(responseDtoList)
                .build();
    }


    public ApiBasicResponse updateDiaryTitle(UpdateDiaryDto updateDiaryDto, PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        if (!diary.getOwner().equals(member)) {
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

        if (!diary.getOwner().equals(member)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diaryRepository.delete(diary);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("다이어리 삭제 성공")
                .build();
    }


    public ApiDataResponse<DiaryInviteResponseDto> inviteToDiary(InviteToDiaryDto inviteToDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(inviteToDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member owner = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if (!diary.getOwner().equals(owner)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        Member invitedMember = memberRepository.findByProfileName(inviteToDiaryDto.getProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        diaryMemberService.inviteMemberToDiary(diary, invitedMember);

        return ApiDataResponse.<DiaryInviteResponseDto>builder()
                .status(true)
                .code(200)
                .message("모임 요청을 성공적으로 보냈습니다. 초대를 받은 멤버 데이터는 아래와 같습니다.")
                .data(DiaryInviteResponseDto.makeFrom(invitedMember))
                .build();
    }

    public ApiDataResponse<DiaryOverViewResponseDto> acceptInvite(AcceptDiaryDto acceptDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = diaryRepository.findById(acceptDiaryDto.getDiaryId())
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_EXCEPTION));

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        DiaryMember diaryMember = diaryMemberRepository.findByDiaryAndMember(diary, member)
                .orElseThrow(() -> new DiaryMemberException(DiaryMemberErrorCode.NO_EXIST_DIARY_MEMBER_EXCEPTION));

        if (diaryMember.isAcceptedInvite()) {
            throw new DiaryMemberException(DiaryMemberErrorCode.ALREADY_ACCEPTED_INVITATION_EXCEPTION);
        }

        diaryMember.acceptInvitation();
        diaryMemberRepository.save(diaryMember);

        List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

        return ApiDataResponse.<DiaryOverViewResponseDto>builder()
                .status(true)
                .code(200)
                .message("초대 수락 성공! 리스트에 추가할 다이어리 데이터는 아래와 같습니다.")
                .data(DiaryOverViewResponseDto.builder()
                        .id(diary.getId())
                        .title(diary.getTitle())
                        .photoUrls(photoUrls)
                        .build())
                .build();
    }
}
