package com.knu.fromnow.api.domain.mission.service;

import com.knu.fromnow.api.domain.diary.dto.response.DiaryReadRowResponseDto;
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

    public MissionList[] getTwoRandomMissions() {
        MissionList[] missions = MissionList.values();
        Random random = new Random();

        int firstIndex = random.nextInt(missions.length);
        int secondIndex;

        // 두 번째 인덱스가 첫 번째 인덱스와 같지 않을 때까지 반복
        do {
            secondIndex = random.nextInt(missions.length);
        } while (firstIndex == secondIndex);

        // 두 미션을 배열에 담아 반환
        return new MissionList[]{missions[firstIndex], missions[secondIndex]};
    }

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
                .date(LocalDate.now())
                .build());
    }

    public ApiDataResponse<List<MissionTodayResponseDto>> getTodayMissionList(LocalDate date) {
        Mission mission = missionRepository.findByDate(date);
        List<MissionTodayResponseDto> data = new ArrayList<>();
        data.add(MissionTodayResponseDto.makeFrom(mission));

        return ApiDataResponse.<List<MissionTodayResponseDto>>builder()
                .status(true)
                .code(200)
                .message("미션 리스트 반환 성공")
                .data(data)
                .build();
    }
}
