package com.knu.fromnow.api.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateMemberDto {
    @NotEmpty(message = "ProfileName 필드가 없거나 요청 스펙이 잘못 되었습니다")
    private String profileName;
    @NotEmpty(message = "identiFier 필드가 없거나 요청 스펙이 잘못 되었습니다")
    private String identifier;
}
