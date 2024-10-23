package com.knu.fromnow.api.domain.diary.controller;


import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadColResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiarySearchResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadCompleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.service.DiaryService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryInviteResponseDto>>> inviteToDiary(
            @RequestBody InviteToDiaryDto inviteToDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<List<DiaryInviteResponseDto>> response = diaryService.inviteToDiary(inviteToDiaryDto, principalDetails);

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

    @GetMapping("/{diaryId}/menu")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryMenuResponseDto>>> getDiaryMenu(
            @PathVariable("diaryId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<DiaryMenuResponseDto>> response = diaryService.getDiaryMenu(id, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    /**
     * 가로 스크롤 Api
     */
    @GetMapping("/diaries/{diaryId}/scroll/row")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryReadRowResponseDto>>> getRowScroll(
            @PathVariable("diaryId") Long diaryId,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<DiaryReadRowResponseDto>> response = diaryService.getRowScroll(diaryId, year, month, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/diaries/{diaryId}/scroll/col")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiaryReadColResponseDto>>> getColScroll(
            @PathVariable("diaryId") Long diaryId,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("num") int num,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<List<DiaryReadColResponseDto>> response = diaryService.getColScroll(diaryId, year, month, num, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    /**
     * 읽음 처리 Api
     */
    @PostMapping("/{diaryId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryReadCompleteResponseDto>> readAllBoardInDiary(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody DateRequestDto dateRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        ApiDataResponse<DiaryReadCompleteResponseDto> response = diaryService.readAllPostsByDate(diaryId, dateRequestDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{diaryId}/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<List<DiarySearchResponseDto>>> searchToInviteDiary(
            @RequestParam(required = true) String profileName,
            @PathVariable("diaryId") Long diaryId
    ){
        ApiDataResponse<List<DiarySearchResponseDto>> response = diaryService.searchMember(diaryId, profileName);

        return ResponseEntity.status(response.getCode()).body(response);
    }

}
