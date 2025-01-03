package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.FcmRequestDto;
import com.knu.fromnow.api.domain.member.dto.response.FcmResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.MemberWithdrawResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.PhotoUrlResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileMemberDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileNameResponseDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.azure.service.AzureBlobStorageService;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ValidationService validationService;
    private final AzureBlobStorageService azureBlobStorageService;

    public ApiBasicResponse duplicateCheckMember(CreateMemberDto createMemberDto) {
        validationService.checkProfileNameUniqueness(createMemberDto.getProfileName());
        return ApiBasicResponse.successResponse("중복되는 이름이 없습니다! 해당 이름을 사용하세요");
    }

    public ApiDataResponse<ProfileNameResponseDto> setProfileName(CreateMemberDto createMemberDto, PrincipalDetails principalDetails) {
        String profileName = createMemberDto.getProfileName();;

        validationService.checkProfileNameUniqueness(profileName);
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        member.updateProfileName(profileName);
        member.setRequiresAdditionalInfoToFalse();
        memberRepository.save(member);

        return ApiDataResponse.successResponse("프로릴 이름 설정 성공!", ProfileNameResponseDto.of(profileName));
    }

    public ApiDataResponse<PhotoUrlResponseDto> setMemberPhoto(MultipartFile file, PrincipalDetails principalDetails) throws IOException {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        String photoUrl = member.getPhotoUrl();

        if (!file.isEmpty()) {
            photoUrl = azureBlobStorageService.uploadImageToAzure(file);
            member.initMemberPhoto(photoUrl);
        }

        return ApiDataResponse.successResponse("수정 한 photoUrl은 다음과 같습니다.", PhotoUrlResponseDto.of(photoUrl));
    }

    public ApiDataResponse<ProfileMemberDto> getMyProfile(PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        ProfileMemberDto profileMemberDto = ProfileMemberDto.from(member);

        return ApiDataResponse.successResponse("마이페이지 프로필 불러오기 성공!", profileMemberDto);
    }

    public ApiDataResponse<FcmResponseDto> updateFcmToken(FcmRequestDto fcmRequestDto, PrincipalDetails principalDetails) {
        Member member = validationService.validateMemberByEmail(principalDetails.getEmail());
        String fcmToken = fcmRequestDto.getFcmToken();

        member.updateFcmToken(fcmToken);
        memberRepository.save(member);

        return ApiDataResponse.successResponse("FCM 토큰 갱신 성공!", FcmResponseDto.of(fcmToken));
    }

    public ApiDataResponse<MemberWithdrawResponseDto> deleteMember(PrincipalDetails principalDetails) {
        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        memberRepository.delete(me);

        return ApiDataResponse.successResponse("회원 탈퇴 성공!", MemberWithdrawResponseDto.from(me));
    }

    public ApiBasicResponse logoutMember(PrincipalDetails principalDetails) {
        Member me = validationService.validateMemberByEmail(principalDetails.getEmail());
        me.logout();
        memberRepository.save(me);

        return ApiBasicResponse.successResponse("로그아웃 성공!");
    }
}
