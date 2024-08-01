package com.knu.fromnow.api.domain.friend.service;

import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.ApiFriendResponse;
import com.knu.fromnow.api.domain.friend.dto.response.FriendSearchResponseDto;
import com.knu.fromnow.api.domain.friend.entity.Friend;
import com.knu.fromnow.api.domain.friend.repository.FriendRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
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
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public ApiFriendResponse<FriendSearchResponseDto> searchFriend(String profileName){
        Member member = memberRepository.findByProfileName(profileName)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        FriendSearchResponseDto friendSearchResponseDto = FriendSearchResponseDto.builder()
                .profileName(member.getProfileName())
                .profilePhotoUrl(member.getPhoto().getPhotoUrl())
                .build();

        return ApiFriendResponse.<FriendSearchResponseDto>builder()
                .status(true)
                .code(200)
                .message("조회 한 ProfileName : " + member.getProfileName())
                .data(friendSearchResponseDto)
                .build();
    }

    public ApiBasicResponse inviteFriend(SentFriendDto sentFriendDto, PrincipalDetails principalDetails){
        Member fromMember = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member toMember = memberRepository.findByProfileName(sentFriendDto.getToProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        Friend sentFriendRequest = Friend.builder()
                .fromMemberId(fromMember.getId())
                .toMemberId(toMember.getId())
                .areWeFriend(true)
                .build();

        Friend receivedFriendRequest = Friend.builder()
                .fromMemberId(toMember.getId())
                .toMemberId(fromMember.getId())
                .areWeFriend(false)
                .fromMemberProfileUrl(fromMember.getPhoto().getPhotoUrl())
                .build();

        friendRepository.save(sentFriendRequest);
        friendRepository.save(receivedFriendRequest);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("친구 요청 보내기 성공!")
                .build();
    }

}
