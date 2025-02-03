package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.entity.DiaryMember;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberRepository;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateLatestPostTimeRepository;
import com.knu.fromnow.api.domain.tracking.repository.DateReadTrackingRepository;
import com.knu.fromnow.api.domain.tracking.service.DateLatestPostTimeService;
import com.knu.fromnow.api.domain.tracking.service.DateReadTrackingService;
import com.knu.fromnow.api.global.error.custom.DiaryException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.DiaryErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryCrudService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMemberService diaryMemberService;
    private final DiaryMemberRepository diaryMemberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final DateReadTrackingService dateReadTrackingService;
    private final DateLatestPostTimeService dateLatestPostTimeService;
    private final ValidationService validationService;
    private final DateReadTrackingRepository dateReadTrackingRepository;
    private final DateLatestPostTimeRepository dateLatestPostTimeRepository;

    public ApiDataResponse<DiaryCreateResponseDto> createDiary(CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        Diary diary = diaryRepository.save(Diary.createBoard(createDiaryDto, member));

        LocalDateTime createdAt = diary.getCreatedAt();
        LocalDate today = createdAt.toLocalDate();

        diaryMemberService.initMemberToDiary(diary, member, createdAt);
        dateReadTrackingService.initDateReadTracking(diary, member, today);
        dateLatestPostTimeService.initDateLatestPostTime(diary, today);

        return ApiDataResponse.successResponse("다이어리 생성 성공", DiaryCreateResponseDto.fromDiary(diary));
    }

    public ApiDataResponse<List<DiaryOverViewResponseDto>> getDiaryOverView(PrincipalDetails principalDetails) {
        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        List<DiaryOverViewResponseDto> responseDtoList = diaryMemberCustomRepository.fetchDiaryOverViewDtosByMe(me);
        responseDtoList.sort(Comparator.comparing(DiaryOverViewResponseDto::getRecivedAt).reversed());

        return ApiDataResponse.successResponse("다이어리 리스트 반환 성공!", responseDtoList);
    }


    public ApiDataResponse<DiaryRequestsReceivedDto> updateDiaryTitle(UpdateDiaryDto updateDiaryDto, PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_ID_EXCEPTION));

        if (!diary.getOwner().equals(member)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diary.updateDiaryTitle(updateDiaryDto);
        diaryRepository.save(diary);

        return ApiDataResponse.successResponse("다이어리 타이틀 수정", DiaryRequestsReceivedDto.from(diary));
    }

    public ApiDataResponse<DiaryDeleteResponseDto> deleteDiary(PrincipalDetails principalDetails, Long diaryId) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NO_EXIST_DIARY_ID_EXCEPTION));

        if (!diary.getOwner().equals(member)) {
            throw new MemberException(MemberErrorCode.NO_OWNER_EXCEPTION);
        }

        diaryRepository.delete(diary);
        dateReadTrackingRepository.deleteAllByDiaryId(diary.getId());
        dateLatestPostTimeRepository.deleteAllByDiaryId(diary.getId());

        return ApiDataResponse.successResponse("다이어리 삭제 성공", DiaryDeleteResponseDto.from(diary));
    }


    public ApiBasicResponse leaveMyDiary(Long diaryId, PrincipalDetails principalDetails) {
        Diary diary = validationService.validateDiaryById(diaryId);
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        DiaryMember diaryMember = validationService.validateMemberAndDiary(member, diary);

        diaryMemberRepository.delete(diaryMember);

        return ApiBasicResponse.successResponse("다이어리 나가기 성공!");
    }
}
