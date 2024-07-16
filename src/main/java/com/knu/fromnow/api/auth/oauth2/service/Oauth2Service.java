package com.knu.fromnow.api.auth.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.auth.oauth2.entity.Oauth2Attribute;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.Role;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2Service {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    public ResponseEntity<ApiBasicResponse> findOrSaveMember(String idToken, String provider) throws ParseException, JsonProcessingException {
        Oauth2Attribute oauth2Attribute;
        switch (provider) {
            case "google":
                oauth2Attribute = getGoogleData(idToken);
                break;
            default:
                throw new RuntimeException("제공하지 않는 인증기관입니다.");
        }

        Map<String, Object> result = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.OK;
        String message = "이미 계정이 있는 유저입니다!";

        Member member = memberRepository.findByEmail(oauth2Attribute.getEmail())
                .orElseGet(() -> {
                    return getMemberByProvider(oauth2Attribute, provider);
                });

        if (member.getProfileName() == null) {
            httpStatus = HttpStatus.CREATED;
            message = "새로 회원가입하는 유저입니다!";
        }

        String accessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtService.createRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        ResponseCookie responseCookie = ResponseCookie.from("Authorization-refresh", refreshToken)
                .httpOnly(true)
                .sameSite("None")
                .maxAge(14 * 24 * 60 *60)
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        headers.add("Authorization", "Bearer " + accessToken);

        ApiBasicResponse apiBasicResponse = ApiBasicResponse.builder()
                .status(true)
                .code(httpStatus.value())
                .message(message)
                .build();

        return ResponseEntity.status(httpStatus.value()).headers(headers).body(apiBasicResponse);
    }

    private Oauth2Attribute getGoogleData(String idToken) throws ParseException, JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String googleApi = "https://oauth2.googleapis.com/tokeninfo";
        String targetUrl = UriComponentsBuilder.fromHttpUrl(googleApi).queryParam("id_token", idToken).build().toUriString();

        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(response.getBody());

        Map<String, Object> body = new ObjectMapper().readValue(jsonBody.toString(), Map.class);

        return Oauth2Attribute.of("google", "sub", body);
    }

    private Member getMemberByProvider(Oauth2Attribute oauth2Attribute, String provider) {
        Member newMember = null;

        if (provider.equals("google")) {
            newMember = Member.builder()
                    .role(Role.ROLE_GOOGLE_USER)
                    .email(oauth2Attribute.getEmail())
                    .build();
        }

        if (provider.equals("kakao")) {
            newMember = Member.builder()
                    .role(Role.ROLE_KAKAO_USER)
                    .email(oauth2Attribute.getEmail())
                    .build();
        }

        return newMember;
    }

}
