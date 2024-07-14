package com.knu.fromnow.api.global.error;

import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberException(MemberException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(e.getMemberErrorCode().getCode())
                .message(e.getMemberErrorCode().getMessage())
                .build();

        return ResponseEntity.status(apiErrorResponse.getCode()).body(apiErrorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){

        log.info("handleHttpRequestMethodNotSupportedException");

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(405)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(405).body(apiErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message("요청 데이터 형식이 잘못 되었습니다. API 요청 스펙을 다시 확인해주세요")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        log.info("handleMethodArgumentNotValidException");

        String errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message(errorMessages)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }
}
