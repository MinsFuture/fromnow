package com.knu.fromnow.api.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.custom.NotValidTokenException;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        } catch (NotValidTokenException e) {
            handleException(response, e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage());
        } catch (UsernameNotFoundException e) {
            handleException(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (MemberException e){
            handleException(response, HttpStatus.resolve(e.getMemberErrorCode().getCode()), e.getMemberErrorCode().getMessage());
        }
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ApiBasicResponse apiJwtResponse = ApiBasicResponse.builder()
                .status(false)
                .code(status.value())
                .message(message)
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(apiJwtResponse));
    }
}