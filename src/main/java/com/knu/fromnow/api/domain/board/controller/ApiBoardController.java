package com.knu.fromnow.api.domain.board.controller;

import com.knu.fromnow.api.domain.board.dto.request.BoardCreateRequestDto;
import com.knu.fromnow.api.domain.board.dto.request.DiaryChooseRequestDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardLikeResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardCreateResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.DiaryChooseResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.TodayBoardResponseDto;
import com.knu.fromnow.api.domain.board.service.BoardService;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
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

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class ApiBoardController implements SwaggerBoardApi {

    private final BoardService boardService;

    @PostMapping(value = "/diaries/{diaryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<BoardCreateResponseDto>> createBoard(
            @PathVariable("diaryId") Long diaryId,
            @RequestPart("uploadPhotos") MultipartFile file,
            @RequestPart("createDiaryDto") BoardCreateRequestDto boardCreateRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        ApiDataResponse<BoardCreateResponseDto> response
                = boardService.createBoard(file, boardCreateRequestDto, diaryId, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping(value = "/diaries", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<DiaryChooseResponseDto>> createBoardAndChooseDiary(
            @RequestPart("uploadPhotos") MultipartFile file,
            @RequestPart("chooseDiaryDto") DiaryChooseRequestDto diaryChooseRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        ApiDataResponse<DiaryChooseResponseDto> response
                = boardService.createBoardAndChooseDiary(file, diaryChooseRequestDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    /**
     * 하루 치 글 불러오기
     */
    @GetMapping("/diaries/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<TodayBoardResponseDto>> getTodayBoards(
            @PathVariable("diaryId") Long diaryId,
            @RequestParam("date") String date,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<TodayBoardResponseDto> boardOverviews = boardService.getTodayBoards(diaryId, date, principalDetails);

        return ResponseEntity.status(boardOverviews.getCode()).body(boardOverviews);
    }


    @PostMapping("/{boardId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<BoardLikeResponseDto>> clickLike(
            @PathVariable("boardId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        ApiDataResponse<BoardLikeResponseDto> response = boardService.clickLike(id, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/{boardId}/dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<BoardLikeResponseDto>> clickDisLike(
            @PathVariable("boardId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ){
        ApiDataResponse<BoardLikeResponseDto> response = boardService.clickDisLike(id, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
