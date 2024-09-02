package com.knu.fromnow.api.domain.board.controller;

import com.knu.fromnow.api.domain.board.dto.CreateBoardDto;
import com.knu.fromnow.api.domain.board.service.BoardService;
import com.knu.fromnow.api.domain.diary.dto.response.ApiDiaryResponse;
import com.knu.fromnow.api.domain.diary.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class ApiBoardController implements BoardApi {

    private final BoardService boardService;

    @PostMapping(value = "/{diaryId}/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiBasicResponse> createBoard(
            @PathVariable("diaryId") Long diaryId,
            @RequestPart("uploadPhotos") MultipartFile[] files,
            @RequestPart("createDiaryDto") CreateBoardDto createBoardDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiBasicResponse apiBasicResponse
                = boardService.createBoard(files, createBoardDto, diaryId, principalDetails);

        return ResponseEntity.status(apiBasicResponse.getCode()).body(apiBasicResponse);
    }

    @GetMapping("/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDiaryResponse<List<BoardOverViewResponseDto>>> getBoardOverviews(
            @PathVariable("diaryId") Long id,
            @RequestParam("date") LocalDate date,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){

        ApiDiaryResponse<List<BoardOverViewResponseDto>> boardOverviews = boardService.getBoardOverviews(id, date, principalDetails);

        return ResponseEntity.status(boardOverviews.getCode()).body(boardOverviews);
    }

}
