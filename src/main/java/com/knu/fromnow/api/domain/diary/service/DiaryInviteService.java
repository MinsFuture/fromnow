package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.ImmediateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.RejectDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryImmeInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.tracking.service.DateReadTrackingService;
import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryMemberErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryInviteService {

    private final ValidationService validationService;
    private final DiaryMemberService diaryMemberService;
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final FirebaseService firebaseService;
    private final DateReadTrackingService dateReadTrackingService;


    public ApiDataResponse<List<DiaryInviteResponseDto>> inviteToDiary(InviteToDiaryDto inviteToDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = validationService.validateDiaryById(inviteToDiaryDto.getDiaryId());
        Member owner = validationService.validateMemberByEmail(principalDetails.getEmail());
        // 후에 로직 수정 -> 엘라스틱 서치
        List<Member> invitedMembers = memberRepository.findByProfileNameIn(inviteToDiaryDto.getProfileNames());
        List<DiaryMember> diaryMembers = diaryMemberRepository.findByDiary(diary);

        for (Member member : invitedMembers) {
            boolean alreadyInvited = diaryMembers.stream()
                    .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

            if (alreadyInvited) {
                throw new MemberException(MemberErrorCode.ALREADY_INVITED_EXCEPTION);
            }
        }

        diaryMemberService.inviteMemberToDiary(diary, invitedMembers);
        List<DiaryInviteResponseDto> data = firebaseService.sendDiaryNotificationToInvitedMembers(owner, invitedMembers, diary);

        return ApiDataResponse.successResponse("모임 요청을 성공적으로 보냈습니다. 초대를 받은 멤버 데이터는 아래와 같습니다.", data);
    }

    public ApiDataResponse<DiaryOverViewResponseDto> acceptInvite(AcceptDiaryDto acceptDiaryDto, PrincipalDetails principalDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        Diary diary = validationService.validateDiaryById(acceptDiaryDto.getDiaryId());
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        DiaryMember diaryMember = validationService.validateMemberAndDiary(member, diary);

        if (diaryMember.isAcceptedInvite()) {
            throw new DiaryMemberException(DiaryMemberErrorCode.ALREADY_ACCEPTED_INVITATION_EXCEPTION);
        }
        diaryMember.acceptInvitation();
        diaryMember.updateRecievedAt(now);
        diaryMemberRepository.save(diaryMember);
        List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

        // tracking 정보 추가하기
        dateReadTrackingService.createTrackingWhenAcceptedDiary(member, diary, today);

        return ApiDataResponse.successResponse("초대 수락 성공! 리스트에 추가할 다이어리 데이터는 아래와 같습니다.", DiaryOverViewResponseDto.of(diary, photoUrls, now));
    }

    public ApiBasicResponse rejectInvite(RejectDiaryDto rejectDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = validationService.validateDiaryById(rejectDiaryDto.getRejectDiaryId());
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        DiaryMember diaryMember = validationService.validateMemberAndDiary(member, diary);
        diaryMemberRepository.delete(diaryMember);

        return ApiBasicResponse.successResponse("다이어리 초대 거절 성공");
    }

    public ApiDataResponse<DiaryImmeInviteResponseDto> immediateInviteToDiary(ImmediateDiaryDto immediateDiaryDto, PrincipalDetails principalDetails) {
        Diary diary = validationService.validateDiaryById(immediateDiaryDto.getDiaryId());
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        List<DiaryMember> diaryMembers = diaryMemberRepository.findByDiary(diary);

        boolean alreadyInvited = diaryMembers.stream()
                .anyMatch(diaryMember -> diaryMember.getMember().equals(member));

        if (alreadyInvited) {
            throw new MemberException(MemberErrorCode.ALREADY_INVITED_EXCEPTION); // Custom error code
        }

        diaryMemberService.immediateInviteMemberToDiary(diary, member);

        return ApiDataResponse.successResponse("모임 요청을 성공적으로 보냈습니다. 다이어리에 추가 된 멤버 데이터는 아래와 같습니다.", DiaryImmeInviteResponseDto.from(member));
    }
}
