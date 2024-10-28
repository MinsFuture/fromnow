package com.knu.fromnow.api.domain.mission.service;

import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.mission.entity.Mission;
import com.knu.fromnow.api.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
}
