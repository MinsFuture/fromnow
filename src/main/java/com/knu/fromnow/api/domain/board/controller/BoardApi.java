package com.knu.fromnow.api.domain.board.controller;

import com.knu.fromnow.api.domain.board.dto.CreateBoardDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BoardApi {
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "일기 생성 성공!"),
                    @ApiResponse(responseCode = "404", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "일기 생성 로직", description = "File은 MultiPart/form-data 형식, Dto는 application/json 형식으로 보내주셔야 해요!")
    ResponseEntity<ApiBasicResponse> createDiary(
            @Parameter(description = "일기 사진들", required = true) MultipartFile[] file,
            @Parameter(description = "일기 내용", required = true) CreateBoardDto createBoardDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);
}
