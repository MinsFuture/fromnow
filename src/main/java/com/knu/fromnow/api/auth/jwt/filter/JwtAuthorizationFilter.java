package com.knu.fromnow.api.auth.jwt.filter;

import com.knu.fromnow.api.auth.jwt.service.JwtService;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.entity.Role;
import com.knu.fromnow.api.domain.member.service.MemberService;
import com.knu.fromnow.api.global.error.custom.JwtTokenException;
import com.knu.fromnow.api.global.error.errorcode.custom.JwtTokenErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // filter를 거치고 싶지 않은 path를 여기서 관리함
        String[] excludePathLists = {"/favicon.ico", "/swagger-ui/index.html", "/api/jwt/access-token"};
        String[] excludePathStartsWithLists = {"/login", "/oauth2", "/api/member/check", "/v3", "/swagger-ui", "/ws", "/api/oauth2", "/azure"};

        String path = request.getRequestURI();

        // 해당 경로로 시작하는 uri에 대해서는 true를 반환하고 filter를 거치지 않음
        boolean startsWithExcludedPath = Arrays.stream(excludePathStartsWithLists).
                anyMatch(path::startsWith);

        // excludePathLists과 같은 uri로 매칭되면 true를 반환하고 filter를 거치지않음.
        boolean matchesExcludedPath = Arrays.stream(excludePathLists)
                .anyMatch((excludePath) -> excludePath.equals(path));

        return startsWithExcludedPath || matchesExcludedPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        // Authorization 헤더가 없는 경우 처리
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtTokenException(JwtTokenErrorCode.NO_EXIST_AUTHORIZATION_HEADER_EXCEPTION);
        }

        String accesstoken = request.getHeader("Authorization").substring(7);

        if (jwtService.validateToken(accesstoken)) {
            //JWT 토큰을 파싱해서 member 정보를 가져옴
            Member member = null;

            if (jwtService.getRole(accesstoken).equals(Role.ROLE_GOOGLE_USER.name())
                    || jwtService.getRole(accesstoken).equals(Role.ROLE_KAKAO_USER.name())) {
                String email = jwtService.getEmail(accesstoken);
                log.info("email : {}", email);
                member = memberService.findByEmail(email);
                log.info("member email : {}", member.getEmail());
            }

            // 해당 멤버를 authentication(인증) 해줌
            authentication(request, response, filterChain, member);
            return;
        }

        filterChain.doFilter(request, response);
    }


    /**
     * 유저를 authentication 해주는 메소드
     */
    private static void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Member member) throws IOException, ServletException {
        // PrincipalDetails에 유저 정보를 담기
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authenticationToken
                = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // 세션에 사용자 등록, 해당 사용자는 스프링 시큐리티에 의해서 인증됨
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        MDC.put("member_email", member.getEmail());

        filterChain.doFilter(request, response);
    }
}
