package com.knu.fromnow.api.domain.mission.dto.response;

import com.knu.fromnow.api.domain.mission.entity.Mission;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionTodayResponseDto {
    private String title;
    private String content;
    private String missionImg;

    @Builder
    public MissionTodayResponseDto(String title, String content, String missionImg) {
        this.title = title;
        this.content = content;
        this.missionImg = missionImg;
    }

    public static MissionTodayResponseDto makeFrom(Mission mission){
        return MissionTodayResponseDto.builder()
                .title(mission.getTitle())
                .content(mission.getContent())
                .missionImg(mission.getMissionImg())
                .build();
    }
}
