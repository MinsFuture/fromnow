package com.knu.fromnow.api.domain.photo.repository;

import com.knu.fromnow.api.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
