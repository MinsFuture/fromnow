package com.knu.fromnow.api.domain.diary.controller;

import com.knu.fromnow.api.domain.diary.dto.CreateBoardDto;
import com.knu.fromnow.api.domain.diary.service.BoardService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/board")
public class ApiBoardController implements BoardApi {

    private final BoardService boardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> createDiary(
            @RequestPart("uploadPhotos") MultipartFile[] files,
            @RequestPart("createDiaryDto") CreateBoardDto createBoardDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiBasicResponse apiBasicResponse
                = boardService.createBoard(files, createBoardDto, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }

}
