package com.knu.fromnow.api.auth.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.knu.fromnow.api.auth.oauth2.service.Oauth2Service;
import com.knu.fromnow.api.domain.member.controller.MemberApi;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minidev.json.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class Oauth2Controller implements Oauth2Api {

    private final Oauth2Service oauth2Service;

    @GetMapping("/google")
    public ResponseEntity<ApiBasicResponse> oauth2Google(
            @RequestParam("id_token") String idToken) throws ParseException, JsonProcessingException {
        return oauth2Service.findOrSaveMember(idToken, "google");
    }

    @GetMapping("/kakao")
    public ResponseEntity<ApiBasicResponse> oauth2Kakao(
            @RequestParam("id_token") String idToken) throws ParseException, JsonProcessingException {
        return oauth2Service.findOrSaveMember(idToken, "kakao");
    }
}
