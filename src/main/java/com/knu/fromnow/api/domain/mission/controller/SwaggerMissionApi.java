package com.knu.fromnow.api.domain.mission.controller;

import com.knu.fromnow.api.domain.diary.dto.request.CreateDiaryDto;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryCreateResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.mission.dto.response.MissionTodayResponseDto;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface SwaggerMissionApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "오늘의 미션 리스트 반환 성공!"),
                    @ApiResponse(responseCode = "4XX", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "오늘의 미션 리스트 반환하기", description = "오늘의 미션 리스트 반환")
    ResponseEntity<ApiDataResponse<List<MissionTodayResponseDto>>> getTodayMission(
            @Parameter(description = "반환 받을 날짜") LocalDate date);

}
