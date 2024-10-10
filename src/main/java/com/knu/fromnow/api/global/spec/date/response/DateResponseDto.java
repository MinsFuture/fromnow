package com.knu.fromnow.api.global.spec.date.response;

import com.knu.fromnow.api.global.spec.date.request.DateRequestDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DateResponseDto {
    private String date;

    @Builder
    public DateResponseDto(String date) {
        this.date = date;
    }

    public static DateResponseDto makeFrom(DateRequestDto dateRequestDto) {
        String formattedDate = String.format("%04d-%02d-%02d",
                dateRequestDto.getYear(),
                dateRequestDto.getMonth(),
                dateRequestDto.getDay());

        return DateResponseDto.builder()
                .date(formattedDate)
                .build();
    }
}
