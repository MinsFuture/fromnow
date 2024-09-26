package com.knu.fromnow.api.domain.friend.controller;

import com.knu.fromnow.api.domain.friend.dto.request.AcceptFriendDto;
import com.knu.fromnow.api.domain.friend.dto.request.SentFriendDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendAcceptResponseDto;
import com.knu.fromnow.api.domain.friend.dto.response.FriendSearchResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SwaggerFriendApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "친구 찾기 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "친구 찾기", description = "profileName으로 부분 문자열 조회합니다")
    ResponseEntity<ApiDataResponse<List<FriendSearchResponseDto>>> searchFriends(
            @Parameter String profileName,
            @Parameter  PrincipalDetails principalDetails
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "친구 요청 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "친구 요청 하기", description = "친구의 profileName으로 요청합니다")
    ResponseEntity<ApiBasicResponse> inviteFriend(
            @Parameter SentFriendDto sentFriendDto,
            @Parameter PrincipalDetails principalDetails
    );

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "친구 수락 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "친구 수락 하기", description = "친구 요청을 수락합니다")
    ResponseEntity<ApiDataResponse<FriendAcceptResponseDto>> acceptFriend(
            @Parameter AcceptFriendDto acceptFriendDto,
            @Parameter PrincipalDetails principalDetails
    );
}
