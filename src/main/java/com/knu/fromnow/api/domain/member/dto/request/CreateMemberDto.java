package com.knu.fromnow.api.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateMemberDto {
    @NotNull(message = "ProfileName은 필드가 없거나 요청 스펙이 잘못 되었습니다")
    @Size(min = 4, max = 12, message = "ProfileName은 4글자에서 12글자 사이여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "ProfileName은 영문/숫자만 가능합니다")
    private String profileName;
    @NotEmpty(message = "identiFier 필드가 없거나 요청 스펙이 잘못 되었습니다")
    private String identifier;
}
