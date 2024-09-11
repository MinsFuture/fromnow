package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryMemberService {
    private final MemberRepository memberRepository;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final DiaryRepository diaryRepository;

    public void inviteMemberToDiary(Diary diary, Member member){
        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .acceptedInvite(false)
                .build();

        diaryMemberRepository.save(diaryMember);
    }

    public ApiDataResponse<List<DiaryRequestsReceivedDto>> getDiaryRequestsReceived(PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> diaryIds = diaryMemberCustomRepository.getUnacceptedDiaryIdsByMember(member);
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);

        List<DiaryRequestsReceivedDto> diaryRequestsReceivedDtos = getDiaryRequestsReceivedDtos(diaryList);

        return ApiDataResponse.<List<DiaryRequestsReceivedDto>>builder()
                .status(true)
                .code(200)
                .message("내가 받은 모임 요청 리스트 반환 성공!")
                .data(diaryRequestsReceivedDtos)
                .build();
    }



    private static List<DiaryRequestsReceivedDto> getDiaryRequestsReceivedDtos(List<Diary> diaryList) {
        List<DiaryRequestsReceivedDto> diaryRequestsReceivedDtos = new ArrayList<>();
        for (Diary diary : diaryList) {
            DiaryRequestsReceivedDto diaryRequestsReceivedDto = DiaryRequestsReceivedDto.builder()
                    .diaryId(diary.getId())
                    .diaryTitle(diary.getTitle())
                    .build();
            diaryRequestsReceivedDtos.add(diaryRequestsReceivedDto);
        }
        return diaryRequestsReceivedDtos;
    }
}
