package com.knu.fromnow.api.domain.member.controller;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DeleteMemberRequestDto;
import com.knu.fromnow.api.domain.member.dto.request.FcmRequestDto;
import com.knu.fromnow.api.domain.member.dto.request.LogoutMemberRequestDto;
import com.knu.fromnow.api.domain.member.dto.response.FcmResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.MemberWithdrawResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.PhotoUrlResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileNameResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.api.ApiErrorResponse;
import com.knu.fromnow.api.global.spec.api.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "회원", description = "회원 관련 api")
public interface MemberApi {

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "프로필 중복 체크 성공!"),
                    @ApiResponse(responseCode = "409",
                            description = "중복 된 프로필 이름이 이미 존재 함",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @Operation(summary = "프로필 이름 중복체크 로직", description = "영어와 숫자로만 이루어진 4~12글자 사이여야 함")
    ResponseEntity<ApiBasicResponse> duplicateCheckMember(
            @Parameter(description = "프로필 이름", required = true) CreateMemberDto createMemberDto);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "프로필 이름 생성 성공!"),
                    @ApiResponse(responseCode = "404", description = "ProfileName 요청이 잘못되었습니다"),
            }
    )
    @Operation(summary = "프로필 이름 생성 로직", description = "영어와 숫자로만 이루어진 4~12글자 사이여야 함")
    ResponseEntity<ApiDataResponse<ProfileNameResponseDto>> setProfileName(
            @Parameter(description = "프로필 이름", required = true) CreateMemberDto createMemberDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);


    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "프로필 사진 생성 성공!"),
                    @ApiResponse(responseCode = "404", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "프로필 사진 생성 로직", description = "MultiPart/form-data 형식으로 보내주셔야 해요!")
    ResponseEntity<ApiDataResponse<PhotoUrlResponseDto>> setMemberPhoto(
            @Parameter(description = "프로필 사진", required = true) MultipartFile file,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Fcm 토큰 갱신 성공!"),
                    @ApiResponse(responseCode = "404", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "Fcm 토큰 갱신 로직", description = "Fcm 토큰 갱신 성공!")
    ResponseEntity<ApiDataResponse<FcmResponseDto>> updateFcmToken(
            @Parameter(description = "Fcm 토큰") FcmRequestDto fcmRequestDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공!"),
                    @ApiResponse(responseCode = "404", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "회원 탈퇴 로직", description = "회원 탈퇴 성공!")
    ResponseEntity<ApiDataResponse<MemberWithdrawResponseDto>> deleteMember(
            @Parameter(description = "회원 탈퇴 정보 - 프로필 이름") DeleteMemberRequestDto deleteMemberRequestDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공!"),
                    @ApiResponse(responseCode = "404", description = "요청 형식이 잘못되었습니다"),
            }
    )
    @Operation(summary = "회원 로그아웃 로직", description = "회원 탈퇴 성공!")
    ResponseEntity<ApiBasicResponse> logoutMember(
            @Parameter(description = "회원 로그아웃 정보 - 프로필 이름") LogoutMemberRequestDto logoutMemberRequestDto,
            @Parameter(description = "Bearer ey...") PrincipalDetails principalDetails);



}