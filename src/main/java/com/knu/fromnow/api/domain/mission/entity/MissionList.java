package com.knu.fromnow.api.domain.mission.entity;

import lombok.Getter;

@Getter
public enum MissionList implements MissionInterface{

    TODAY_WEATHER_MISSION("고개를 들어 하늘을 봐", "오늘의 하늘 촬영하기",  "weather.png"),
    TODAY_OOTD_MISSION("오늘 내 패션 점수는 몇 점?", "OOTD 찍고 패션 점수 매기기",  "fashion.png"),
    TODAY_FOOD_MISSION("이븐하게 익었네요, 합격!", "오늘 먹은 점심식사 자랑하기", "food.png"),
    TODAY_COOKING_MISSION("오늘은 내가 짜파게티 요리사", "직접 만든 요리 자랑하기", "cook.png"),
    TODAY_RANDOM_IMAGE("갤러리 단속반 떴다!", "갤러리 내 랜덤 이미지 공유하기", "random.png"),
    TODAY_GOAL_MISSION("2025년에는 꼭 갓생 살거야!", "2025년 이루고 싶은 목표 공유하기", "goal.png"),
    TODAY_MUSIC_MISSION("나라가 허락한 유일한 마약, MUSIC ...", "내 Playlist와 함께 노래 추천하기", "music.png"),
    TODAY_SAD_MISSION("슬픔을 둘로 나누면? 슬과 픔", "최근 힘들었던 순간 공유하고 위로하기", "sad.png"),
    TODAY_HAPPY_MISSION("기쁨은 둘로 나누면 두 배!", "최근 행복했던 순간 공유하고 위로하기", "happy.png"),
    TODAY_WISHLIST_MISSION("내 위시리스트 구경할래?" ,"사고 싶은 물건을 공유하기", "wishlist.png"),
    TODAY_HABIT_MISSION("제 취미는요 ...", "요즘 빠진 취미 자랑하기", "habit.png");

    private static final String BASE_URL = "https://fromnowstorage.blob.core.windows.net/fromnowcontainer/mission_";

    MissionList(String title, String content, String missionImg) {
        this.title = title;
        this.content = content;
        this.missionImg = BASE_URL + missionImg;
    }

    private String title;
    private String content;
    private String missionImg;

}
