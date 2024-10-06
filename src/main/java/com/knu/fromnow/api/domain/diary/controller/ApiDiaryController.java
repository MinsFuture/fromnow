package com.knu.fromnow.api.domain.diary.controller;


import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.service.DiaryService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
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
    public ResponseEntity<ApiDataResponse<DiaryCreateResponseDto>> createDiary(
            @RequestBody CreateDiaryDto createDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ApiDataResponse<DiaryCreateResponseDto> response = diaryService.createDiary(createDiaryDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryOverViewResponseDto>>> getDiaryOverView(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<List<DiaryOverViewResponseDto>> diaryOverView = diaryService.getDiaryOverView(principalDetails);

        return ResponseEntity.status(diaryOverView.getCode()).body(diaryOverView);
    }

    @GetMapping("/{diaryId}/menu")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryMenuResponseDto>>> getDiaryMenu(
            @PathVariable("diaryId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<DiaryMenuResponseDto>> response = diaryService.getDiaryMenu(id, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryInviteResponseDto>> inviteToDiary(
            @RequestBody InviteToDiaryDto inviteToDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<DiaryInviteResponseDto> response = diaryService.inviteToDiary(inviteToDiaryDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryOverViewResponseDto>> acceptedInvite(
            @RequestBody AcceptDiaryDto acceptDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<DiaryOverViewResponseDto> response = diaryService.acceptInvite(acceptDiaryDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }


    @PutMapping("{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryRequestsReceivedDto>> updateDiaryTitle(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody UpdateDiaryDto updateDiarydto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<DiaryRequestsReceivedDto> response = diaryService.updateDiaryTitle(updateDiarydto, principalDetails, diaryId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryDeleteResponseDto>> deleteDiary(
            @PathVariable("diaryId") Long diaryId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<DiaryDeleteResponseDto> response = diaryService.deleteDiary(principalDetails, diaryId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

}
