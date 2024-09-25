package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.response.PhotoUrlResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileMemberDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileNameResponseDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.repository.BoardPhotoRepository;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardPhotoService boardPhotoService;
    private final JwtService jwtService;
    private final BoardPhotoRepository boardPhotoRepository;

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
        memberRepository.save(member);

        return ApiDataResponse.<ProfileNameResponseDto>builder()
                .status(true)
                .code(200)
                .message("프로필 사진 설정 성공!")
                .data(ProfileNameResponseDto.builder()
                        .profileName(createMemberDto.getProfileName())
                        .build())
                .build();
    }

    public ApiDataResponse<PhotoUrlResponseDto> setMemberPhoto(MultipartFile file, PrincipalDetails principalDetails){
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        String photoUrl = boardPhotoService.uploadImageToGcs(file);
        member.setMemberPhoto(photoUrl);

        return ApiDataResponse.<PhotoUrlResponseDto>builder()
                .status(true)
                .code(200)
                .message("수정 한 photoUrl은 다음과 같습니다")
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
}
