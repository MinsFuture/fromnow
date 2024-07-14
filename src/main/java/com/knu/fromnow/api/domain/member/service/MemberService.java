package com.knu.fromnow.api.domain.member.service;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DuplicateCheckDto;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.Role;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

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


    /**
     * 초기 앱 진입 시 ProfileName 설정 로직
     *
     * @param createMemberDto
     * @return ApiBasicResponse
     */
    public ApiBasicResponse createMember(CreateMemberDto createMemberDto){
        if(memberRepository.existsByProfileName(createMemberDto.getProfileName())){
            throw new MemberException(MemberErrorCode.CONFLICT_PROFILE_NAME_MEMBER_EXCEPTION);
        }

        Member member = Member.builder()
                .role(Role.ROLE_BASIC_USER)
                .identifier(createMemberDto.getIdentifier())
                .profileName(createMemberDto.getProfileName())
                .build();

        memberRepository.save(member);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("멤버 가입 성공!")
                .build();
    }
}
