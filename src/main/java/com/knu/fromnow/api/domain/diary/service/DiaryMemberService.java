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
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryMemberService {
    private final MemberRepository memberRepository;
    private final ValidationService validationService;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final DiaryRepository diaryRepository;

    public void initMemberToDiary(Diary diary, Member member, LocalDateTime createdAt){
        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .recievedAt(createdAt)
                .acceptedInvite(true)
                .build();

        member.getDiaryMembers().add(diaryMember);
        diary.getDiaryMembers().add(diaryMember);

        diaryMemberRepository.save(diaryMember);
    }

    public void inviteMemberToDiary(Diary diary, List<Member> invitedMembers){

        List<DiaryMember> diaryMembers = new ArrayList<>();

        for (Member invitedMember : invitedMembers) {
            DiaryMember diaryMember = DiaryMember.builder()
                    .diary(diary)
                    .member(invitedMember)
                    .acceptedInvite(false)
                    .build();

            diaryMembers.add(diaryMember);
        }

       diaryMemberRepository.saveAll(diaryMembers);
    }

    public void immediateInviteMemberToDiary(Diary diary, Member member){
        DiaryMember diaryMember = DiaryMember.builder()
                .diary(diary)
                .member(member)
                .acceptedInvite(true)
                .build();

        diaryMemberRepository.save(diaryMember);
    }


    public ApiDataResponse<List<DiaryRequestsReceivedDto>> getDiaryRequestsReceived(PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());

        List<Long> diaryIds = diaryMemberCustomRepository.getUnacceptedDiaryIdsByMember(member);
        List<Diary> diaryList = diaryRepository.findByIdIn(diaryIds);

        List<DiaryRequestsReceivedDto> list = new ArrayList<>();

        for (Diary diary : diaryList) {
            List<String> photoUrls = diaryMemberCustomRepository.fetchMemberPhotoUrlsByDiary(diary);

            DiaryRequestsReceivedDto diaryRequestsReceivedDto = DiaryRequestsReceivedDto.builder()
                    .diaryTitle(diary.getTitle())
                    .diaryId(diary.getId())
                    .photoUrls(photoUrls)
                    .build();

            list.add(diaryRequestsReceivedDto);
        }

        return ApiDataResponse.successResponse("내가 받은 모임 요청 리스트 반환 성공!", list);
    }

}
