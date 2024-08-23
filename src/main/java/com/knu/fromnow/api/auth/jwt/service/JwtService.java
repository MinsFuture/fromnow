package com.knu.fromnow.api.auth.jwt.service;

import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.error.custom.NotValidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.knu.fromnow.api.global.error.errorcode.custom.JwtTokenErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private SecretKey secretKey;

    @Value("${spring.jwt.secret}")
    private String secret;
    private final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60;


    private final MemberRepository memberRepository;

    @PostConstruct
    private void init() {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getIdentifier(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("identifier", String.class);

    }

    public String getProfileName(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("profileName", String.class);
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new NotValidTokenException(NOT_VALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            if (isRefreshTokenExpired(e)) {
                throw new NotValidTokenException(EXPIRED_REFRESH_TOKEN_EXCEPTION);
            }
            throw new NotValidTokenException(EXPIRED_ACCESS_TOKEN_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new NotValidTokenException(UNSUPPORTED_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new NotValidTokenException(MISMATCH_CLAIMS_EXCEPTION);
        }
    }

    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .claim("token_type", "access_token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken() {
        Long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 14L;
        return Jwts.builder()
                .claim("token_type", "refresh_token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(14 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public String getAccessTokenFromRefresh(String refreshToken){
        if(refreshToken == null){
            throw new NotValidTokenException(REFRESH_TOKEN_COOKIE_NULL_EXCEPTION);
        }

        validateToken(refreshToken);
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotValidTokenException(REFRESH_TOKEN_MISMATCH_EXCEPTION));

        String accessToken = createAccessToken(member.getEmail(), member.getRole().name());

        return accessToken;
    }

    private static boolean isRefreshTokenExpired(ExpiredJwtException e) {
        return e.getClaims().get("token_type").equals("refresh_token");
    }
}
