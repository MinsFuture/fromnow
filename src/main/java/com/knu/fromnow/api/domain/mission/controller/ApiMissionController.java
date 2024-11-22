package com.knu.fromnow.api.domain.mission.controller;

import com.knu.fromnow.api.domain.mission.dto.response.MissionTodayResponseDto;
import com.knu.fromnow.api.domain.mission.service.MissionService;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class ApiMissionController implements SwaggerMissionApi{
    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<ApiDataResponse<List<MissionTodayResponseDto>>> getTodayMission(
            @RequestParam("date") LocalDate date){

        ApiDataResponse<List<MissionTodayResponseDto>> response = missionService.getTodayMissionList(date);

        return ResponseEntity.status(response.getCode()).body(response);
    }


}
