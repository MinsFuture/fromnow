package com.knu.fromnow.api.domain.member.controller;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DuplicateCheckDto;
import com.knu.fromnow.api.global.error.dto.ApiErrorResponse;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

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
            @Parameter(description = "프로필 이름", required = true) DuplicateCheckDto duplicateCheckDto);

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "멤버 생성 성공!"),
                    @ApiResponse(responseCode = "409",
                            description = "중복 된 프로필 이름이 이미 존재 함",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @Operation(summary = "멤버 생성 로직", description = "멤버 생성 로직")
    ResponseEntity<ApiBasicResponse> createMember(
            @Parameter(description = "프로필 이름", required = true) CreateMemberDto createMemberDto);
}