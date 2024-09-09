package com.knu.fromnow.api.auth.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.minidev.json.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원", description = "회원 관련 api")
public interface Oauth2Api {

    @Operation(summary = "Google 로그인 로직", description = "id_token을 쿼리파라미터 형식으로 보내주면 됨")
    ResponseEntity<ApiBasicResponse> oauth2Google(
            @Parameter(description = "id_token", required = true) String id_token) throws ParseException, JsonProcessingException;

    @Operation(summary = "Kakao 로그인 로직", description = "id_token을 쿼리파라미터 형식으로 보내주면 됨")
    ResponseEntity<ApiBasicResponse> oauth2Kakao(
            @Parameter(description = "id_token", required = true) String id_token) throws ParseException, JsonProcessingException;

}
