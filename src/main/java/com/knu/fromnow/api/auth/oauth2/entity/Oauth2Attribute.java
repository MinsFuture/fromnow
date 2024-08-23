package com.knu.fromnow.api.auth.oauth2.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Oauth2Attribute {
    private String provider;
    private String userId;
    private String username;
    private String email;

    public static Oauth2Attribute of(String provider, String usernameAttributeName, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return Oauth2Attribute.ofGoogle(provider, usernameAttributeName, attributes);
            case "kakao":
                return Oauth2Attribute.ofKakao(provider, attributes);
            default:
                throw new RuntimeException("소셜 로그인 접근 실패");
        }

    }

    private static Oauth2Attribute ofGoogle(String provider, String usernameAttributeName, Map<String, Object> attributes) {

        return Oauth2Attribute.builder()
                .provider(provider)
                .username(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .userId(String.valueOf(attributes.get(usernameAttributeName)).concat("google"))
                .build();
    }

    private static Oauth2Attribute ofKakao(String provider, Map<String, Object> attributes) {

        return Oauth2Attribute.builder()
                .provider(provider)
                .username(String.valueOf(attributes.get("nickname")))
                .email(String.valueOf(attributes.get("email")))
                .build();
    }
}