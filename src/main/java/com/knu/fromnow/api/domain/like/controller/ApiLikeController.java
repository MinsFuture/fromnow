package com.knu.fromnow.api.domain.like.controller;

import com.knu.fromnow.api.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class ApiLikeController {
    
    private final LikeService likeService;

}
