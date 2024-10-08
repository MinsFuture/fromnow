package com.knu.fromnow.api.domain.read.controller;

import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.read.dto.BoardReadResponseDto;
import com.knu.fromnow.api.domain.read.service.BoardReadService;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/read")
public class ApiBoardReadController implements SwaggerBoardReadApi{

    private final BoardReadService boardReadService;

    @PostMapping("/{boardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<BoardReadResponseDto>> readBoard(
            @PathVariable("boardId") Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        ApiDataResponse<BoardReadResponseDto> response = boardReadService.readBoard(id, principalDetails);

       return ResponseEntity.status(response.getCode()).body(response);
    }
}
