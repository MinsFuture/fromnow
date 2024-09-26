package com.knu.fromnow.api.domain.friend.service;

import com.knu.fromnow.api.domain.friend.dto.request.AcceptFriendDto;
import com.knu.fromnow.api.domain.friend.dto.request.FriendDeleteRequestDto;
import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendAcceptResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendDeleteResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendSearchResponseDto;
import com.knu.fromnow.api.domain.friend.entity.Friend;
import com.knu.fromnow.api.domain.friend.repository.FriendCustomRepository;
import com.knu.fromnow.api.domain.friend.repository.FriendRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberCustomRepository;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.FriendException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.FriendErrorCode;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private static final Logger log = LoggerFactory.getLogger(FriendService.class);
    private final FriendCustomRepository friendCustomRepository;
    private final FriendRepository friendRepository;
    private final MemberCustomRepository memberCustomRepository;
    private final MemberRepository memberRepository;

    public ApiDataResponse<List<FriendSearchResponseDto>> searchFriend(String profileName, PrincipalDetails principalDetails) {
        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Member> members = memberCustomRepository.findMembersByProfileNameContainingIgnoreCase(profileName);
        List<Long> memberIds = members.stream().map((m) -> m.getId()).toList();

        List<Long> friendsAmongSpecificMembers = friendCustomRepository.findFriendsAmongSpecificMembers(me.getId(), memberIds);

        if (members.isEmpty()) {
            throw new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION);
        }

        List<FriendSearchResponseDto> friendsDtos = new ArrayList<>();

        for (Member member : members) {
            boolean isFriend = friendsAmongSpecificMembers.contains(member.getId());

            friendsDtos.add(FriendSearchResponseDto.builder()
                    .memberId(member.getId())
                    .profileName(member.getProfileName())
                    .profilePhotoUrl(member.getPhotoUrl())
                    .isFriend(isFriend)
                    .build());
        }

        return ApiDataResponse.<List<FriendSearchResponseDto>>builder()
                .status(true)
                .code(200)
                .message("프로필 이름으로 검색 성공")
                .data(friendsDtos)
                .build();
    }

    public ApiDataResponse<List<FriendBasicResponseDto>> getAllMyFriend(PrincipalDetails principalDetails) {
        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> friendIds = friendCustomRepository.findAllFriendsWithEachOther(me.getId());
        List<Member> friends = memberRepository.findByIdIn(friendIds);

        List<FriendBasicResponseDto> friendsDtos = new ArrayList<>();

        for (Member member : friends) {
            friendsDtos.add(FriendBasicResponseDto.builder()
                    .memberId(member.getId())
                    .profileName(member.getProfileName())
                    .profilePhotoUrl(member.getPhotoUrl())
                    .build());
        }

        return ApiDataResponse.<List<FriendBasicResponseDto>>builder()
                .status(true)
                .code(200)
                .message("내 친구들 찾아오기 성공!")
                .data(friendsDtos)
                .build();
    }

    public ApiBasicResponse inviteFriend(SentFriendDto sentFriendDto, PrincipalDetails principalDetails) {
        Member fromMember = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member toMember = memberRepository.findByProfileName(sentFriendDto.getSentProfileName())
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
                .fromMemberProfileUrl(fromMember.getPhotoUrl())
                .build();

        friendRepository.save(sentFriendRequest);
        friendRepository.save(receivedFriendRequest);

        return ApiBasicResponse.builder()
                .status(true)
                .code(200)
                .message("친구 요청 보내기 성공!")
                .build();
    }

    public ApiDataResponse<FriendAcceptResponseDto> acceptFriend(AcceptFriendDto acceptFriendDto, PrincipalDetails principalDetails) {

        // 나
        Member fromMember = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        // 수락 당하는 애
        Member toMember = memberRepository.findById(acceptFriendDto.getAcceptMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        Friend friend = friendRepository.findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())
                .orElseThrow(() -> new FriendException(FriendErrorCode.INVALID_FRIEND_REQUEST_EXCEPTION));

        if (friend.isAreWeFriend()) {
            throw new FriendException(FriendErrorCode.ALREADY_WE_ARE_FRIEND_EXCEPTION);
        }

        friend.acceptFriend();
        friendRepository.save(friend);

        return ApiDataResponse.<FriendAcceptResponseDto>builder()
                .status(true)
                .code(200)
                .message("수락한 친구 데이터는 다음과 같습니다")
                .data(FriendAcceptResponseDto.builder()
                        .id(toMember.getId())
                        .photoUrl(toMember.getPhotoUrl())
                        .profileName(toMember.getProfileName())
                        .isFriend(true)
                        .build())
                .build();
    }

    public ApiDataResponse<List<FriendBasicResponseDto>> getFriendRequestsReceived(PrincipalDetails principalDetails) {
        // from이 나인데, are we가 false인 모든 to들을 가져오기
        Member fromMember = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        List<Long> receivedFriendRequests
                = friendCustomRepository.findAllRequestReceived(fromMember.getId());
        List<Member> receivedMembers = memberRepository.findByIdIn(receivedFriendRequests);

        List<FriendBasicResponseDto> friendsDtos = new ArrayList<>();
        for (Member member : receivedMembers) {
            friendsDtos.add(FriendBasicResponseDto.builder()
                    .memberId(member.getId())
                    .profileName(member.getProfileName())
                    .profilePhotoUrl(member.getPhotoUrl())
                    .build());
        }

        return ApiDataResponse.<List<FriendBasicResponseDto>>builder()
                .status(true)
                .code(200)
                .message("친구 수락 대기중인 녀석들 가져오기 성공")
                .data(friendsDtos)
                .build();
    }

    public ApiDataResponse<FriendDeleteResponseDto> deleteFriend(FriendDeleteRequestDto friendDeleteRequestDto, PrincipalDetails principalDetails) {

        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member deleteFriend = memberRepository.findById(friendDeleteRequestDto.getDeleteId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_MEMBER_ID_EXCEPTION));


        List<Long> friendIds = friendCustomRepository.findMutualFriends(me.getId(), deleteFriend.getId());

        for (Long friendId : friendIds) {
            friendRepository.deleteById(friendId);
        }

        return ApiDataResponse.<FriendDeleteResponseDto>builder()
                .status(true)
                .code(200)
                .message("삭제 한 친구의 데이터는 다음과 같습니다")
                .data(FriendDeleteResponseDto.builder()
                        .id(deleteFriend.getId())
                        .profileName(deleteFriend.getProfileName())
                        .photoUrl(deleteFriend.getPhotoUrl())
                        .isFriend(false)
                        .build())
                .build();
    }
}
