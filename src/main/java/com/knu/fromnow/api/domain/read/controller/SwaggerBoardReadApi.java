package com.knu.fromnow.api.domain.read.controller;

import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.read.dto.BoardReadResponseDto;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface SwaggerBoardReadApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 읽음 처리 Api"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 읽음 처리 Api", description = "다이어리 읽음 처리 Api")
    ResponseEntity<ApiDataResponse<BoardReadResponseDto>> readBoard(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);
}
