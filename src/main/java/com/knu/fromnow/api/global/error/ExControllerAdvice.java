package com.knu.fromnow.api.global.error;

import com.knu.fromnow.api.global.error.custom.DiaryMemberException;
import com.knu.fromnow.api.global.error.custom.FriendException;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.custom.JwtTokenException;
import com.knu.fromnow.api.global.spec.ApiErrorResponse;
import com.knu.fromnow.api.global.error.errorcode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    // 공통적으로 사용되는 메서드
    private static ApiErrorResponse buildErrorResponseWithCustomException(ErrorCode errorCode) {
        return ApiErrorResponse.builder()
                .status(false)
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }

    private static ApiErrorResponse buildErrorResponseWithGeneralException(String message, HttpStatus httpStatus){
        return ApiErrorResponse.builder()
                .status(false)
                .code(httpStatus.value())
                .message(message)
                .build();
    }

    private static ResponseEntity<ApiErrorResponse> buildResponseEntity(ApiErrorResponse apiErrorResponse){
        log.error("ERROR [ {} ][ {} ][ {} ] - Exception: {}", MDC.get("request_id"), MDC.get("member_email"), MDC.get("request_uri"), apiErrorResponse.getMessage());
        return ResponseEntity.status(apiErrorResponse.getCode()).body(apiErrorResponse);
    }

    // MemberException 처리
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberException(MemberException e) {
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithCustomException(e.getMemberErrorCode());
        return buildResponseEntity(apiErrorResponse);
    }

    // FriendException 처리
    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ApiErrorResponse> handleFriendException(FriendException e) {
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithCustomException(e.getFriendErrorCode());
        return buildResponseEntity(apiErrorResponse);
    }

    // NotValidTokenException 처리
    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleNotValidTokenException(JwtTokenException e){
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithCustomException(e.getJwtTokenErrorCode());
        return buildResponseEntity(apiErrorResponse);
    }

    // DiaryMemberException 처리
    @ExceptionHandler(DiaryMemberException.class)
    public ResponseEntity<ApiErrorResponse> handleDiaryMemberException(DiaryMemberException e){
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithCustomException(e.getDiaryMemberErrorCode());
        return buildResponseEntity(apiErrorResponse);
    }

    /**
     * Http Method가 잘못 됨 -> Get인데 Post 등
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        String errorMessage = e.getMessage();
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.METHOD_NOT_ALLOWED);

        return buildResponseEntity(apiErrorResponse);
    }

    /**
     * Api 요청 스펙이 잘못 됨 -> Json 형식으로 안보냄
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        String errorMessage = "요청 데이터 형식이 잘못 되었습니다. API 요청 스펙을 다시 확인해주세요";
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.BAD_REQUEST);

        return buildResponseEntity(apiErrorResponse);
    }

    /**
     * Validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.BAD_REQUEST);

        return buildResponseEntity(apiErrorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpClientErrorException(HttpClientErrorException e){
        String errorMessage = e.getMessage();
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.BAD_REQUEST);

        return buildResponseEntity(apiErrorResponse);
    }

    /**
     * MultiPart/Form-data 등으로 안보내는 등
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e){
        String errorMessage = e.getMessage() + "\n Dto는 application/json 타입, Photo는 multipart/form-data 타입";
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        return buildResponseEntity(apiErrorResponse);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiErrorResponse> handleMultipartException(MultipartException e){
        String errorMessage = "요청 형식은 Multipart request여야 합니다";
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.NOT_FOUND);

        return buildResponseEntity(apiErrorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException e){
        String errorMessage = "해당 경로에 매핑되는 컨트롤러가 없습니다. 요청 경로를 확인하세요";
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage, HttpStatus.NOT_FOUND);

        return buildResponseEntity(apiErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(Exception e) {
        String errorMessage ="예상치 못한 에러가 발생했습니다.";
        ApiErrorResponse apiErrorResponse = buildErrorResponseWithGeneralException(errorMessage + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        return buildResponseEntity(apiErrorResponse);
    }
}
