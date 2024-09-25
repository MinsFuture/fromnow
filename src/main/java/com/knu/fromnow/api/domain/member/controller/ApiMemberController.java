package com.knu.fromnow.api.domain.member.controller;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.response.PhotoUrlResponseDto;
import com.knu.fromnow.api.domain.member.dto.response.ProfileNameResponseDto;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.service.MemberService;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import com.knu.fromnow.api.global.spec.ApiDataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class ApiMemberController implements MemberApi {

    private final MemberService memberService;

    @PostMapping("/check")
    public ResponseEntity<ApiBasicResponse> duplicateCheckMember(
            @RequestBody @Valid CreateMemberDto createMemberDto) {
        ApiBasicResponse response = memberService.duplicateCheckMember(createMemberDto);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/profileName")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<ProfileNameResponseDto>> setProfileName(
            @RequestBody @Valid CreateMemberDto createMemberDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiDataResponse<ProfileNameResponseDto> response = memberService.setProfileName(createMemberDto, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiDataResponse<PhotoUrlResponseDto>> setMemberPhoto(
            @RequestPart("uploadPhoto") MultipartFile file,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        ApiDataResponse<PhotoUrlResponseDto> response = memberService.setMemberPhoto(file, principalDetails);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
