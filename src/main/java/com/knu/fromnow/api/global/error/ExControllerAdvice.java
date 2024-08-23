package com.knu.fromnow.api.global.error;

import com.knu.fromnow.api.global.error.custom.FriendException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.custom.NotValidTokenException;
import com.knu.fromnow.api.global.error.dto.ApiErrorResponse;
import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    // 공통적으로 사용되는 메서드
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(ErrorCode errorCode) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(apiErrorResponse.getCode()).body(apiErrorResponse);
    }

    // MemberException 처리
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberException(MemberException e) {
        return buildErrorResponse(e.getMemberErrorCode());
    }

    // FriendException 처리
    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ApiErrorResponse> handleFriendException(FriendException e) {
        return buildErrorResponse(e.getFriendErrorCode());
    }

    //
    @ExceptionHandler(NotValidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleNotValidTokenException(NotValidTokenException e){

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(e.getErrorCode().getHttpStatus().value())
                .message(e.getErrorCode().getMessage())
                .data(e.getErrorCode().getData())
                .build();

        return ResponseEntity.status(apiErrorResponse.getCode()).body(apiErrorResponse);
    }

    /**
     * Http Method가 잘못 됨 -> Get인데 Post 등
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(405)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(405).body(apiErrorResponse);
    }

    /**
     * Api 요청 스펙이 잘못 됨 -> Json 형식으로 안보냄
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message("요청 데이터 형식이 잘못 되었습니다. API 요청 스펙을 다시 확인해주세요")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    /**
     * Validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
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

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpClientErrorException(HttpClientErrorException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    /**
     * MultiPart/Form-data 등으로 안보내는 등
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(415)
                .message(e.getMessage() + "\n Dto는 application/json 타입, Photo는 multipart/form-data 타입")
                .build();

        return ResponseEntity.status(415).body(apiErrorResponse);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiErrorResponse> handleMultipartException(MultipartException e){
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .status(false)
                .code(404)
                .message("요청 형식은 Multipart request여야 합니다")
                .build();

        return ResponseEntity.status(404).body(apiErrorResponse);
    }
}
