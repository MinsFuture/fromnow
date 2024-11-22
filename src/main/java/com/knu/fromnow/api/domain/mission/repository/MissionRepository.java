package com.knu.fromnow.api.domain.mission.repository;

import com.knu.fromnow.api.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByDateOrderByCreatedAtAsc(LocalDate date);
}
