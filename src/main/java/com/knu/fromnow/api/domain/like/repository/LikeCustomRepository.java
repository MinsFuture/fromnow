package com.knu.fromnow.api.domain.like.repository;

import java.util.List;

public interface LikeCustomRepository {
    List<Long> findAllBoardsWithMyLikes(Long memberId);


}
