package com.knu.fromnow.api.domain.board.controller;

import com.knu.fromnow.api.domain.board.dto.request.BoardCreateRequestDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardLikeResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardCreateResponseDto;
import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface SwaggerBoardApi {
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "일기 생성 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "일기 생성 로직", description = "File은 MultiPart/form-data 형식, Dto는 application/json 형식으로 보내주셔야 해요!")
    ResponseEntity<ApiDataResponse<BoardCreateResponseDto>> createBoard(
            @Parameter(description = "다이어리 id", required = true) Long diaryId,
            @Parameter(description = "일기 사진들", required = true) MultipartFile[] file,
            @Parameter(description = "일기 내용", required = true) BoardCreateRequestDto boardCreateRequestDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "일기 조회 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "하루 치 일기 내용들 조회", description = "다이어리 안에서 하루 치 날짜에 해당하는 일기들의 내용을 반환")
    ResponseEntity<ApiDataResponse<List<BoardOverViewResponseDto>>> getBoardOverviews(
            @Parameter(description = "일기 id") Long id,
            @Parameter(description = "하루 날짜, 예시 : 2024-09-09") LocalDate date,
            @Parameter PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "일기에 좋아요 누르기", description = "일기에 좋아요를 눌러요")
    ResponseEntity<ApiDataResponse<BoardLikeResponseDto>> clickLike(
            @Parameter Long id,
            @Parameter PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "일기에 좋아요 취소하기", description = "일기에 좋아요를 취소하기")
    ResponseEntity<ApiDataResponse<BoardLikeResponseDto>> clickDisLike(
            @Parameter Long id,
            @Parameter PrincipalDetails principalDetails);

}
