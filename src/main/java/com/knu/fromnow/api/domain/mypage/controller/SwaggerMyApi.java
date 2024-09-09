package com.knu.fromnow.api.domain.mypage.controller;

import com.knu.fromnow.api.domain.diary.dto.response.BoardOverViewResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendBasicResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SwaggerMyApi {

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
    ResponseEntity<ApiDataResponse<List<FriendBasicResponseDto>>> getRequestsReceived(
            @Parameter PrincipalDetails principalDetails);
}
