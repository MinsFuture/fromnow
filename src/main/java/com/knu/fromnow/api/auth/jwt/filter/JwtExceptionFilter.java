package com.knu.fromnow.api.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.custom.JwtTokenException;
import com.knu.fromnow.api.global.spec.ApiErrorDataResponse;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtTokenException e) {
            handleTokenException(response, e);
        } catch (MemberException e) {
            handleMemberException(response, e);
        }
    }

    private void handleTokenException(HttpServletResponse response, JwtTokenException e) throws IOException {
        ApiErrorDataResponse<String> apiErrorResponse = ApiErrorDataResponse.<String>builder()
                .status(false)
                .code(e.getJwtTokenErrorCode().getHttpStatus().value())
                .message(e.getJwtTokenErrorCode().getMessage())
                .data(e.getJwtTokenErrorCode().getData().toString())
                .build();

        log.error("ERROR [{}][{}] - Exception: {}", MDC.get("request_id"), MDC.get("request_uri"), apiErrorResponse.getMessage());

        response.setStatus(apiErrorResponse.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }

    private void handleMemberException(HttpServletResponse response, MemberException e) throws IOException {
        ApiBasicResponse apiErrorResponse = ApiBasicResponse.builder()
                .status(false)
                .code(e.getMemberErrorCode().getHttpStatus().value())
                .message(e.getMemberErrorCode().getMessage())
                .build();

        log.error("ERROR [{}][{}] - Exception: {}", MDC.get("request_id"), MDC.get("request_uri"), apiErrorResponse.getMessage());

        response.setStatus(apiErrorResponse.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}