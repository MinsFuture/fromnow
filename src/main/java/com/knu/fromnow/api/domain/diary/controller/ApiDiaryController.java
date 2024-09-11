package com.knu.fromnow.api.domain.diary.controller;


import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.ApiDiaryResponse;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.service.DiaryService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class ApiDiaryController implements SwaggerDiaryApi {

    private final DiaryService diaryService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> createDiary(
            @RequestBody CreateDiaryDto createDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiBasicResponse apiBasicResponse = diaryService.createDiary(createDiaryDto, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }

    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDiaryResponse<List<DiaryOverViewResponseDto>>> getDiaryOverView(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDiaryResponse<List<DiaryOverViewResponseDto>> diaryOverView = diaryService.getDiaryOverView(principalDetails);

        return ResponseEntity.status(diaryOverView.getCode()).body(diaryOverView);
    }

    @PostMapping("/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> inviteToDiary(
            @RequestBody InviteToDiaryDto inviteToDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiBasicResponse response = diaryService.inviteToDiary(inviteToDiaryDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> acceptedInvite(
            @RequestBody AcceptDiaryDto acceptDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiBasicResponse response = diaryService.acceptInvite(acceptDiaryDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }


    @PutMapping("{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> updateDiaryTitle(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody UpdateDiaryDto updateDiarydto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiBasicResponse response = diaryService.updateDiaryTitle(updateDiarydto, principalDetails, diaryId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> deleteDiary(
            @PathVariable("diaryId") Long diaryId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiBasicResponse response = diaryService.deleteDiary(principalDetails, diaryId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

}
