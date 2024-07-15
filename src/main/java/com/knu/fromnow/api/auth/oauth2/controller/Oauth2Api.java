package com.knu.fromnow.api.auth.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.minidev.json.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원", description = "회원 관련 api")
public interface Oauth2Api {
    ResponseEntity<ApiBasicResponse> oauth2Google(
            @Parameter(description = "id_token", required = true) String id_token) throws ParseException, JsonProcessingException;
}
