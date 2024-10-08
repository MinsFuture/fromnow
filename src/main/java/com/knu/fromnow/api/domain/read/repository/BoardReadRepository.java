package com.knu.fromnow.api.domain.read.repository;


import com.knu.fromnow.api.domain.read.entity.BoardRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReadRepository extends JpaRepository<BoardRead, Long> {
}
