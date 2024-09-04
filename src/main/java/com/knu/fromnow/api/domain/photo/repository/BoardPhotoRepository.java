package com.knu.fromnow.api.domain.photo.repository;

import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPhotoRepository extends JpaRepository<BoardPhoto, Long> {
}
