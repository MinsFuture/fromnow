package com.knu.fromnow.api.domain.mypage.controller;

import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.service.DiaryMemberService;
import com.knu.fromnow.api.domain.diary.service.DiaryService;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.friend.service.FriendService;
import com.knu.fromnow.api.domain.like.service.LikeService;
import com.knu.fromnow.api.domain.member.dto.response.ProfileMemberDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.service.MemberService;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my")
public class ApiMyController implements SwaggerMyApi{

    private final LikeService likeService;
    private final FriendService friendService;
    private final DiaryMemberService diaryMemberService;
    private final MemberService memberService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<ProfileMemberDto>> getMyProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<ProfileMemberDto> response = memberService.getMyProfile(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/likes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<BoardOverViewResponseDto>>> getAllMyLikeBoards(
            @AuthenticationPrincipal PrincipalDetails principalDetails){

        ApiDataResponse<List<BoardOverViewResponseDto>> response = likeService.getAllMyLikeBoards(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend/mutual")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<FriendBasicResponseDto>>> getAllMyFriend(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<FriendBasicResponseDto>> response = friendService.getAllMyFriend(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend/requests/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<FriendBasicResponseDto>>> getFriendRequestsReceived(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<FriendBasicResponseDto>> response = friendService.getFriendRequestsReceived(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/diary/requests/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryRequestsReceivedDto>>> getDiaryRequestsReceived(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<DiaryRequestsReceivedDto>> response = diaryMemberService.getDiaryRequestsReceived(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

}
