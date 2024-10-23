package com.knu.fromnow.api.global.firebase.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.firebase.dto.response.FirebaseTestResponseDto;
import com.knu.fromnow.api.global.firebase.service.FirebaseService;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/firebase")
public class FirebaseController {

    private final FirebaseService firebaseService;

    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<FirebaseTestResponseDto>> testNotification(
            @AuthenticationPrincipal PrincipalDetails principalDetails) throws FirebaseMessagingException {
        ApiDataResponse<FirebaseTestResponseDto> response = firebaseService.testNotification(principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }


}
