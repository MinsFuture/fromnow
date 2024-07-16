package com.knu.fromnow.api.domain.diary.controller;

import com.knu.fromnow.api.domain.diary.dto.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.service.DiaryService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class ApiDiaryController {

    private final DiaryService diaryService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> createDiary(
            @RequestPart("uploadPhotos") MultipartFile[] files,
            @RequestPart("createDiaryDto") CreateDiaryDto createDiaryDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiBasicResponse apiBasicResponse
                = diaryService.createDiary(files, createDiaryDto, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }

}
