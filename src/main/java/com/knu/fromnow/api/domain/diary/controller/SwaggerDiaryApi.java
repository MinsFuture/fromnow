package com.knu.fromnow.api.domain.diary.controller;

import com.knu.fromnow.api.domain.diary.dto.request.AcceptDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.ImmediateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.InviteToDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.RejectDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.request.UpdateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryDeleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryMenuResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadColResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadCompleteResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiarySearchResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
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
    ResponseEntity<ApiDataResponse<List<DiaryInviteResponseDto>>> inviteToDiary(
            @Parameter(description = "초대 할 다이어리 id와, 초대 받을 사람들의 profileNames") InviteToDiaryDto inviteToDiaryDto,
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
                    @ApiResponse(responseCode = "200", description = "다이어리 초대 거절"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 초대를 거절하는 api", description = "초대를 거절합니다")
    ResponseEntity<ApiBasicResponse> rejectedInvite(
            @Parameter(description = "거절 할 다이어리 id") RejectDiaryDto rejectDiaryDto,
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
    ResponseEntity<ApiDataResponse<DiaryRequestsReceivedDto>> updateDiaryTitle(
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
    ResponseEntity<ApiDataResponse<DiaryDeleteResponseDto>> deleteDiary(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 메뉴 가져오기 성공"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 메뉴 가져오기", description = "다이어리 사이드 메뉴 바 정보")
    ResponseEntity<ApiDataResponse<List<DiaryMenuResponseDto>>> getDiaryMenu(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    //////////////////////////////////////////////////////////////////////////////////////////////
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "가로 스크롤 Api - 한달 치"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "가로 스크롤 Api - 한달 치", description = "가로 스크롤 Api - 한달 치")
    ResponseEntity<ApiDataResponse<List<DiaryReadRowResponseDto>>> getRowScroll(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "불러올 년도") int year,
            @Parameter(description = "불러올 월") int month,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails)
    ;

    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "세로 스크롤 Api - X달 치"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "세로 스크롤 Api - X달 치", description = "세로 스크롤 Api - X달 치")
    ResponseEntity<ApiDataResponse<List<DiaryReadColResponseDto>>> getColScroll(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "불러올 년도") int year,
            @Parameter(description = "불러올 월") int month,
            @Parameter(description = "불러올 월 갯수") int num,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);


    //////////////////////////////////////////////////////////////////////////////////////////////

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "읽음 처리 APi"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "읽음 처리 APi", description = "읽음 처리 APi")
    ResponseEntity<ApiDataResponse<DiaryReadCompleteResponseDto>> readAllBoardInDiary(
            @Parameter(description = "다이어리 id") Long diaryId,
            @Parameter(description = "읽음 처리 할 날짜") DateRequestDto dateRequestDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails
    );

    /////////////////////////////////////////////////////////////////////////////////////////////
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 초대 검색 APi"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 초대 검색 APi", description = "다이어리 초대 검색 APi")
    ResponseEntity<ApiDataResponse<List<DiarySearchResponseDto>>> searchToInviteDiary(
            @Parameter(description = "프로필 이름") String profileName,
            @Parameter(description = "다이어리 id") Long diaryId
    );

    /////////////////////////////////////////////////////////////////////////////////////////////
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "다이어리 즉시 초대 API"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "다이어리 즉시 초대 APi", description = "다이어리 즉시 초대 APi")
    ResponseEntity<ApiDataResponse<List<DiaryInviteResponseDto>>> immediatelyInviteToDiary(
            @Parameter(description = "초대 할 다이어리와 프로필 이름") ImmediateDiaryDto immediateDiaryDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails
    );



}
