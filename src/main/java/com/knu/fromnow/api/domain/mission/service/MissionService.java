package com.knu.fromnow.api.domain.mission.service;

import com.knu.fromnow.api.domain.mission.dto.response.MissionTodayResponseDto;
import com.knu.fromnow.api.domain.mission.entity.Mission;
import com.knu.fromnow.api.domain.mission.entity.MissionList;
import com.knu.fromnow.api.domain.mission.repository.MissionRepository;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionList getRandomMission() {
        MissionList[] missions = MissionList.values();
        Random random = new Random();
        int randomIndex = random.nextInt(missions.length);
        return missions[randomIndex];
    }

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    @Transactional
    public void initTodayMission() {
        MissionList mission = getRandomMission();

        missionRepository.save(Mission.builder()
                .title(mission.getTitle())
                .content(mission.getContent())
                .missionImg(mission.getMissionImg())
                .date(LocalDate.now())
                .build());
    }

    public ApiDataResponse<List<MissionTodayResponseDto>> getTodayMissionList(LocalDate date) {
        Mission mission = missionRepository.findByDate(date);
        List<MissionTodayResponseDto> data = new ArrayList<>();
        data.add(MissionTodayResponseDto.from(mission));

        return ApiDataResponse.successResponse("오늘의 미션 반환", data);
    }
}
