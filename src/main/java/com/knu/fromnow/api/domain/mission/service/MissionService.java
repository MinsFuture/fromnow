package com.knu.fromnow.api.domain.mission.service;

import com.knu.fromnow.api.domain.mission.entity.Mission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MissionService {
    public Mission getRanomMission(){
        Mission[] missions = Mission.values();
        Random random = new Random();

        return missions[random.nextInt(missions.length)];
    }
}
