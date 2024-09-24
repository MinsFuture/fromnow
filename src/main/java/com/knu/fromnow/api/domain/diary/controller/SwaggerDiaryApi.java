package com.knu.fromnow.api.domain.diary.controller;

import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SwaggerDiaryApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 생성 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 생성하기", description = "diaryType은 PERSONAL, SHARE 둘 중 하나로 보내주셔야 합니다")
    ResponseEntity<ApiDataResponse<DiaryCreateResponseDto>> createDiary(
            @Parameter(description = "다이어리 생성 dto") CreateDiaryDto createDiaryDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 리스트 조회 성공"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "홈 화면에 다이어리 리스트 조회", description = "홈 화면에 다이어리 리스트를 조회합니다")
    ResponseEntity<ApiDataResponse<List<DiaryOverViewResponseDto>>> getDiaryOverView(
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 초대 성공"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리에 초대하는 api", description = "다이어리에 초대하는 api")
    ResponseEntity<ApiDataResponse<DiaryInviteResponseDto>> inviteToDiary(
            @Parameter(description = "초대 할 다이어리 id와, 초대 받을 사람의 profileName") InviteToDiaryDto inviteToDiaryDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 삭제"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 초대를 수락하는 api", description = "초대를 수락합니다")
    ResponseEntity<ApiDataResponse<DiaryOverViewResponseDto>> acceptedInvite(
            @Parameter(description = "수락 할 다이어리 id") AcceptDiaryDto acceptDiaryDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails
    );

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 이름 수정"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 이름 수정", description = "다이어리 이름을 수정합니다")
    ResponseEntity<ApiBasicResponse> updateDiaryTitle(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "다이어리 업데이트 dto") UpdateDiaryDto updateDiarydto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 삭제"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 삭제", description = "다이어리를 삭제합니다")
    ResponseEntity<ApiBasicResponse> deleteDiary(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////



}
