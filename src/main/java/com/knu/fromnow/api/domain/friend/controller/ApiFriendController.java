package com.knu.fromnow.api.domain.friend.controller;

import com.knu.fromnow.api.domain.friend.dto.request.AcceptFriendDto;
import com.knu.fromnow.api.domain.friend.dto.request.FriendDeleteRequestDto;
import com.knu.fromnow.api.domain.friend.dto.request.FriendRejectRequestDto;
import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendAcceptResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendDeleteResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendRejectResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendSearchResponseDto;
import com.knu.fromnow.api.domain.friend.service.FriendService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class ApiFriendController implements SwaggerFriendApi{

    private final FriendService friendService;

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<FriendSearchResponseDto>>> searchFriends(
            @RequestParam(required = true) String profileName,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<FriendSearchResponseDto>> response = friendService.searchFriend(profileName, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> inviteFriend(
            @RequestBody SentFriendDto sentFriendDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        ApiBasicResponse response = friendService.inviteFriend(sentFriendDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<FriendAcceptResponseDto>> acceptFriend(
            @RequestBody AcceptFriendDto acceptFriendDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        ApiDataResponse<FriendAcceptResponseDto> response = friendService.acceptFriend(acceptFriendDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<FriendRejectResponseDto>> rejectFriend(
            @RequestBody FriendRejectRequestDto friendRejectRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        ApiDataResponse<FriendRejectResponseDto> response = friendService.rejectFriend(friendRejectRequestDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiDataResponse<FriendDeleteResponseDto>> deleteFriend(
            @RequestBody FriendDeleteRequestDto friendDeleteRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<FriendDeleteResponseDto> response = friendService.deleteFriend(friendDeleteRequestDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
