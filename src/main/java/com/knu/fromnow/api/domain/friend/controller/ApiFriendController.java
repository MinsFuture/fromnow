package com.knu.fromnow.api.domain.friend.controller;

import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.ApiFriendResponse;
import com.knu.fromnow.api.domain.friend.dto.response.FriendSearchResponseDto;
import com.knu.fromnow.api.domain.friend.service.FriendService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class ApiFriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<ApiFriendResponse<FriendSearchResponseDto>> searchFriend(
            @RequestParam String profileName
    ){
        ApiFriendResponse<FriendSearchResponseDto> response = friendService.searchFriend(profileName);

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
}
