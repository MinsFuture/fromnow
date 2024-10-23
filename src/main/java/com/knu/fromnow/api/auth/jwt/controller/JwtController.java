package com.knu.fromnow.api.auth.jwt.controller;

import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
public class JwtController implements SwaggerJwtApi{

    private static final Logger log = LoggerFactory.getLogger(JwtController.class);
    private final JwtService jwtService;

    /**
     * 리프레시로 액세스 토큰 받아오기
     */
    @GetMapping("/access-token")
    public ResponseEntity<ApiBasicResponse> getAccessToken(
            @CookieValue(value = "Authorization-refresh") String refreshToken){

        log.info("token : {}", refreshToken);
        String accessToken = jwtService.getAccessTokenFromRefresh(refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .body(ApiBasicResponse.builder()
                        .status(true)
                        .code(HttpStatus.OK.value())
                        .message("Access Token 재발급 성공")
                        .build());
    }
}
