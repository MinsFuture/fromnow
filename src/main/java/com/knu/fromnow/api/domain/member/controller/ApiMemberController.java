package com.knu.fromnow.api.domain.member.controller;

import com.knu.fromnow.api.domain.member.dto.request.CreateMemberDto;
import com.knu.fromnow.api.domain.member.dto.request.DuplicateCheckDto;
import com.knu.fromnow.api.domain.member.service.MemberService;
import com.knu.fromnow.api.global.spec.ApiBasicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class ApiMemberController {

    private final MemberService memberService;

    @PostMapping("/check")
    public ResponseEntity<ApiBasicResponse> duplicateCheckMember(
            @RequestBody @Valid DuplicateCheckDto duplicateCheckDto){
        ApiBasicResponse response = memberService.duplicateCheckMember(duplicateCheckDto);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiBasicResponse> createMember(
            @RequestBody @Valid CreateMemberDto createMemberDto){
        ApiBasicResponse response = memberService.createMember(createMemberDto);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
