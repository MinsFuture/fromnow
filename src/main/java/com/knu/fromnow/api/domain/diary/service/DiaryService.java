package com.knu.fromnow.api.domain.diary.service;

import com.knu.fromnow.api.domain.diary.dto.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.repository.DiaryRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.service.PhotoService;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final PhotoService photoService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    public ApiBasicResponse createDiary(MultipartFile[] files, CreateDiaryDto createDiaryDto, PrincipalDetails principalDetails){

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Diary diary = Diary.builder()
                .member(member)
                .build();

        photoService.uploadPhoto(files, diary);

        ApiBasicResponse apiBasicResponse = ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("일기 생성 성공!")
                .build();

        return apiBasicResponse;
    }
}
