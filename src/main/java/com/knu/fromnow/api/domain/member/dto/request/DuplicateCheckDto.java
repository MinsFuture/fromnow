package com.knu.fromnow.api.domain.member.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DuplicateCheckDto {
    @NotNull(message = "ProfileName은 null일 수 없습니다")
    @Size(min = 4, max = 12, message = "ProfileName은 4글자에서 12글자 사이여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "ProfileName은 영문/숫자만 가능합니다")
    private String profileName;
}
