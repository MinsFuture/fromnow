package com.knu.fromnow.api.domain.mission.repository;

import com.knu.fromnow.api.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
