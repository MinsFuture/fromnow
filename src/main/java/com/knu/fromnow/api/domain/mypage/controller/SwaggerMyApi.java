package com.knu.fromnow.api.domain.mypage.controller;

import com.knu.fromnow.api.domain.board.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryRequestsReceivedDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileMemberDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface SwaggerMyApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "내 프로필 불러오기 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "내가 프로필 불러오기", description = "내가 프로필 불러오기")
    ResponseEntity<ApiDataResponse<ProfileMemberDto>> getMyProfile(
            @Parameter PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "내가 좋아요 누른 글 불러오기 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "내가 좋아요 한 글 불러오기", description = "내가 좋아요 한 글 불러오기")
    ResponseEntity<ApiDataResponse<List<BoardOverViewResponseDto>>> getAllMyLikeBoards(
            @Parameter PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "서로 친구 조회 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "서로 친구 조회", description = "서로 친구 불러오기")
    ResponseEntity<ApiDataResponse<List<FriendBasicResponseDto>>> getAllMyFriend(
            @Parameter PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "받은 친구 요청 조회 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "받은 친구 요청 조회", description = "받은 친구 요청 조회")
    ResponseEntity<ApiDataResponse<List<FriendBasicResponseDto>>> getFriendRequestsReceived(
            @Parameter PrincipalDetails principalDetails);


    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "초대받은 다이어리 리스트 반환 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "초대받은 다이어리 리스트 반환", description = "초대받은 다이어리 리스트 반환")
    ResponseEntity<ApiDataResponse<List<DiaryRequestsReceivedDto>>> getDiaryRequestsReceived(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
