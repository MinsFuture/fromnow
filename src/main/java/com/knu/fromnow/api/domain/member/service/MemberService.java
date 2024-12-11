package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DeleteMemberRequestDto;
import com.knu.fromnow.api.domain.member.dto.request.FcmRequestDto;
import com.knu.fromnow.api.domain.member.dto.request.LogoutMemberRequestDto;
import com.knu.fromnow.api.domain.member.dto.response.FcmResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.MemberWithdrawResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.PhotoUrlResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileMemberDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileNameResponseDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.global.azure.service.AzureBlobStorageService;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
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
    private final BoardPhotoService boardPhotoService;
    private final AzureBlobStorageService azureBlobStorageService;

    /**
     * ProfileName 중복 체크 로직
     *
     * @param createMemberDto
     * @return ApiBasicResponse
     */
    public ApiBasicResponse duplicateCheckMember(CreateMemberDto createMemberDto){
        if(memberRepository.existsByProfileName(createMemberDto.getProfileName())){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        }

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("중복되는 이름이 없습니다! 해당 이름을 사용하세요")
                .build();
    }

    public ApiDataResponse<ProfileNameResponseDto> setProfileName(CreateMemberDto createMemberDto, PrincipalDetails principalDetails){
        if(memberRepository.existsByProfileName(createMemberDto.getProfileName())){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        };

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        member.setProfileName(createMemberDto.getProfileName());
        if(member.getPhotoUrl() == null){
            member.setMemberPhoto(boardPhotoService.getRandomImageFromAzure());
        }
        member.setRequiresAdditionalInfoToFalse();
        memberRepository.save(member);

        return ApiDataResponse.<ProfileNameResponseDto>builder()
                .status(true)
                .code(200)
                .message("프로필 이름 설정 성공!")
                .data(ProfileNameResponseDto.builder()
                        .profileName(createMemberDto.getProfileName())
                        .build())
                .build();
    }

    public ApiDataResponse<PhotoUrlResponseDto> setMemberPhoto(MultipartFile file, PrincipalDetails principalDetails) throws IOException {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        String photoUrl = member.getPhotoUrl();
        if (!file.isEmpty()) {
            photoUrl = azureBlobStorageService.uploadImageToAzure(file);
            member.setMemberPhoto(photoUrl);
        }

        return ApiDataResponse.<PhotoUrlResponseDto>builder()
                .status(true)
                .code(200)
                .message("수정 한 photoUrl은 다음과 같습니다. File이 null일 경우 Random 이미지가 들어있음.")
                .data(PhotoUrlResponseDto.builder().photoUrl(photoUrl)
                        .build())
                .build();
    }


    public Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
    }

    public ApiDataResponse<ProfileMemberDto> getMyProfile(PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        ProfileMemberDto profileMemberDto = ProfileMemberDto.builder()
                .profileName(member.getProfileName())
                .photoUrl(member.getPhotoUrl())
                .build();

        return ApiDataResponse.<ProfileMemberDto>builder()
                .status(true)
                .code(200)
                .message("마이페이지 프로필 불러오기 성공!")
                .data(profileMemberDto)
                .build();
    }

    public ApiDataResponse<FcmResponseDto> updateFcmToken(FcmRequestDto fcmRequestDto, PrincipalDetails principalDetails) {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
        String fcmToken = fcmRequestDto.getFcmToken();

        member.updateFcmToken(fcmToken);
        memberRepository.save(member);

        return ApiDataResponse.<FcmResponseDto>builder()
                .status(true)
                .code(200)
                .message("FCM 토큰 갱신 성공!")
                .data(FcmResponseDto.builder()
                        .fcmToken(fcmToken)
                        .build())
                .build();
    }

    public ApiDataResponse<MemberWithdrawResponseDto> deleteMember(DeleteMemberRequestDto deleteMemberRequestDto, PrincipalDetails principalDetails) {

        Member findMember = memberRepository.findByProfileName(deleteMemberRequestDto.getProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION));

        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if(!Objects.equals(findMember.getId(), me.getId())){
            throw new MemberException(MemberErrorCode.CANNOT_DELETE_MEMBER);
        }

        memberRepository.delete(me);

        return ApiDataResponse.<MemberWithdrawResponseDto>builder()
                .status(true)
                .code(200)
                .message("회원 탈퇴 성공!")
                .data(MemberWithdrawResponseDto.makeFrom(me))
                .build();
    }

    public ApiBasicResponse logoutMember(LogoutMemberRequestDto logoutMemberRequestDto, PrincipalDetails principalDetails) {
        Member findMember = memberRepository.findByProfileName(logoutMemberRequestDto.getProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_MATCHING_MEMBER_EXCEPTION));

        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        if(!Objects.equals(findMember.getId(), me.getId())){
            throw new MemberException(MemberErrorCode.CANNOT_LOGOUT_MEMBER);
        }

        me.logout();
        memberRepository.save(me);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("로그아웃 성공!")
                .build();
    }
}
