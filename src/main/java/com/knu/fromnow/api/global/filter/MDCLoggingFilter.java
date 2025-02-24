package com.knu.fromnow.api.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(MDCLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // ✅ 요청을 ContentCachingRequestWrapper로 감싸기
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        String requestId = UUID.randomUUID().toString();
        String requestURI = wrappedRequest.getRequestURI();
        String requestParams = getRequestParams(wrappedRequest);

        // ✅ 필터 체인 실행 (wrappedRequest 전달)
        filterChain.doFilter(wrappedRequest, servletResponse);

        // ✅ Body를 필터 실행 후 읽기
        String requestBody = getRequestBody(wrappedRequest);

        MDC.put("request_id", requestId);
        MDC.put("request_uri", requestURI);
        MDC.put("request_params", requestParams);
        MDC.put("request_body", requestBody);

        try {
            log.info("REQUEST [ {} ][ {} ] - Params: {} - Body: {}", requestId, requestURI, requestParams, requestBody);
        } catch (Exception e) {
            log.error("ERROR [ {} ][ {} ] - Exception: {}", requestId, requestURI, e.getMessage());
        } finally {
            log.info("RESPONSE [ {} ][ {} ] - User: {}", MDC.get("request_id"), requestURI, MDC.get("member_email"));
            MDC.clear();
        }
    }

    private String getRequestParams(HttpServletRequest request) {
        return request.getParameterMap()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return new String(content, StandardCharsets.UTF_8);
    }
}
