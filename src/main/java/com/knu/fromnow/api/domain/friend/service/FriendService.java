package com.knu.fromnow.api.domain.friend.service;

import com.knu.fromnow.api.domain.friend.dto.request.AcceptFriendDto;
import com.knu.fromnow.api.domain.friend.dto.request.FriendDeleteRequestDto;
import com.knu.fromnow.api.domain.friend.dto.request.FriendRejectRequestDto;
import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendAcceptResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendDeleteResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendRejectResponseDto;
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
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendCustomRepository friendCustomRepository;
    private final FriendRepository friendRepository;
    private final MemberCustomRepository memberCustomRepository;
    private final MemberRepository memberRepository;
    private final FirebaseService firebaseService;

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
            if(!member.getId().equals(me.getId())){
                boolean isFriend = friendsAmongSpecificMembers.contains(member.getId());
                friendsDtos.add(FriendSearchResponseDto.builder()
                        .memberId(member.getId())
                        .profileName(member.getProfileName())
                        .profilePhotoUrl(member.getPhotoUrl())
                        .isFriend(isFriend)
                        .build());
            }
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
                    .isFriend(true)
                    .build());
        }

        return ApiDataResponse.<List<FriendBasicResponseDto>>builder()
                .status(true)
                .code(200)
                .message("내 친구들 찾아오기 성공!")
                .data(friendsDtos)
                .build();
    }

    public ApiDataResponse<MemberNotificationStatusDto> inviteFriend(SentFriendDto sentFriendDto, PrincipalDetails principalDetails){
        Member fromMember = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member toMember = memberRepository.findByProfileName(sentFriendDto.getSentProfileName())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_PROFILE_NAME_MEMBER_EXCEPTION));

        if (friendRepository.existsByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())) {
            throw new FriendException(FriendErrorCode.ALREADY_WE_ARE_FRIEND_EXCEPTION);
        }

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
        MemberNotificationStatusDto responseDto
                = firebaseService.sendFriendNotificationToInvitedMember(fromMember, toMember);

        return ApiDataResponse.<MemberNotificationStatusDto>builder()
                .status(true)
                .code(200)
                .message("친구 요청 보내기 성공")
                .data(responseDto)
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
                        .memberId(toMember.getId())
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
                    .isFriend(false)
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
                        .memberId(deleteFriend.getId())
                        .profileName(deleteFriend.getProfileName())
                        .photoUrl(deleteFriend.getPhotoUrl())
                        .isFriend(false)
                        .build())
                .build();
    }

    public ApiDataResponse<FriendRejectResponseDto> rejectFriend(FriendRejectRequestDto friendRejectRequestDto, PrincipalDetails principalDetails) {
        Member me = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        Member rejectedMember = memberRepository.findById(friendRejectRequestDto.getRejectedMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NO_EXIST_MEMBER_ID_EXCEPTION));

        Friend deletedFriend1 = friendRepository.findByFromMemberIdAndToMemberIdAndAreWeFriendTrue(rejectedMember.getId(), me.getId())
                .orElseThrow(() -> new FriendException(FriendErrorCode.NO_REQUEST_EXIST_FRIEND_EXCEPTION));

        Friend deletedFriend2 = friendRepository.findByFromMemberIdAndToMemberIdAndAreWeFriendFalse(me.getId(), rejectedMember.getId())
                .orElseThrow(() -> new FriendException(FriendErrorCode.NO_INVITED_EXIST_FREIND_EXCEPTION));

        friendRepository.delete(deletedFriend1);
        friendRepository.delete(deletedFriend2);

        return ApiDataResponse.<FriendRejectResponseDto>builder()
                .status(true)
                .code(200)
                .message("친구 요청을 거절하였습니다. 거절 한 친구의 데이터는 다음과 같아요")
                .data(FriendRejectResponseDto.makeFrom(rejectedMember))
                .build();
    }
}
