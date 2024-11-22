package com.knu.fromnow.api.domain.mission.entity;

import lombok.Getter;

@Getter
public enum MissionList implements MissionInterface{
    TODAY_WEATHER_MISSION("날씨 촬영", "오늘 날씨는 어때? 고개 들어 하늘 사진을 찍어봐!"),
    TODAY_OOTD_MISSION("코디 촬영", "오늘 내 패션 어때? OOTD 찍어서 자랑해줘!"),
    TODAY_EATING_MISSION("식사 촬영", "오늘 뭐 먹었어? 사진으로 공유해줘!"),
    TODAY_MUSIC_MISSION("음악 촬영", "요즘 푹 빠진 내 Playlist 보여주고 노래 추천해줘!"),
    TODAY_FUNNY_MOMENT_MISSION("웃짤 공유", "최근 가장 웃겼던 순간을 보여주고 같이 웃어보자!"),
    TODAY_PAIN_MISSION("고통 공유", "최근 가장 힘들었던 순간을 보여주고 같이 위로해보자!"),
    TODAY_WISHLIST_MISSION("위시리스트 공유" ,"사고 싶은 물건을 공유해봐! 누가 몰래 선물할지도 몰라"),
    TODAY_HABIT_MISSION("취미 공유", "요즘 푹 빠진 내 취미를 공개할게!");

    MissionList(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private String title;
    private String content;
}
