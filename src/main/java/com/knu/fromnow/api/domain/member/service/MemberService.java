package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DuplicateCheckDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.Role;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    /**
     * ProfileName 중복 체크 로직
     *
     * @param duplicateCheckDto
     * @return ApiBasicResponse
     */
    public ApiBasicResponse duplicateCheckMember(DuplicateCheckDto duplicateCheckDto){
        if(memberRepository.existsByProfileName(duplicateCheckDto.getProfileName())){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        }

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("중복되는 이름이 없습니다! 해당 이름을 사용하세요")
                .build();
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));
    }
}
