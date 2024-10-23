package com.knu.fromnow.api.auth.jwt.controller;

import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface SwaggerJwtApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "액세스 토큰 발급 성공"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "리프레시 토큰으로 액세스 토큰 발급하기", description = "리프레시 토큰으로 액세스 토큰을 발급합니다")
    ResponseEntity<ApiBasicResponse> getAccessToken(
            @Parameter(description = "리프레시 토큰") String refreshToken
    );
}
