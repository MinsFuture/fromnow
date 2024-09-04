package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.photo.repository.BoardPhotoRepository;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
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

    public ApiBasicResponse setProfileName(CreateMemberDto createMemberDto, PrincipalDetails principalDetails){
        if(memberRepository.existsByProfileName(createMemberDto.getProfileName())){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        };

        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        member.setProfileName(createMemberDto.getProfileName());
        memberRepository.save(member);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("프로필 이름 설정 성공!")
                .build();
    }

    public ApiBasicResponse setMemberPhoto(MultipartFile file, PrincipalDetails principalDetails){
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        String photoUrl = boardPhotoService.uploadImageToGcs(file);
        member.setMemberPhoto(photoUrl);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("프로필 사진 설정 성공!")
                .build();
    }


    public Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
    }

}
