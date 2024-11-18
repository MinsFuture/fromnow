package com.knu.fromnow.api.global.firebase.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.entity.PrincipalDetails;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.domain.mission.entity.Mission;
import com.knu.fromnow.api.domain.mission.service.MissionService;
import com.knu.fromnow.api.global.error.custom.MemberException;
import com.knu.fromnow.api.global.error.errorcode.custom.MemberErrorCode;
import com.knu.fromnow.api.global.firebase.dto.response.FirebaseTestResponseDto;
import com.knu.fromnow.api.global.spec.api.ApiDataResponse;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FirebaseService {

    private final MemberRepository memberRepository;
    private final DiaryMemberCustomRepository diaryMemberCustomRepository;
    private final MissionService missionService;
    private final ObjectMapper objectMapper;

    public ApiDataResponse<FirebaseTestResponseDto> testNotification(PrincipalDetails principalDetails) throws FirebaseMessagingException {
        Member member = memberRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.No_EXIST_EMAIL_MEMBER_EXCEPTION));

        String fcmToken = member.getFcmToken();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("테스트용")
                        .setBody("테스트 메시지")
                        .build())
                .putData("path", "/camera")
                .build();

        FirebaseTestResponseDto data = FirebaseTestResponseDto.builder()
                .message(FirebaseMessaging.getInstance().send(message))
                .build();

        return ApiDataResponse.<FirebaseTestResponseDto>builder()
                .status(true)
                .code(200)
                .message("테스트")
                .data(data)
                .build();
    }

    // 유저별 알림 전송 메서드
    private void sendNotificationToUser(String fcmToken, Mission mission) throws FirebaseMessagingException {
        String title = mission.getTitle();
        String content = mission.getContent();
        String randomUUID = getRandomUUID();

        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", content);
        data.put("id", randomUUID);
        data.put("path", "Camera");

        System.out.println("title = " + title);
        System.out.println("content = " + content);

        Message message = Message.builder()
                .setToken(fcmToken)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .build();

        // Firebase로 알림 전송
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }

    @Scheduled(cron = "0 0 14 * * ?")  // 매일 오후 2시에 실행
    public void sendNotificationAtTwoPM() throws FirebaseMessagingException {
        // 모든 유저의 FCM 토큰을 조회, NUll은 제외
        List<String> fcmTokens = memberRepository.findAllFcmTokens();

        Mission mission = missionService.getRanomMission();

        // 모든 유저에게 알림 발송
        for (String fcmToken : fcmTokens) {
            sendNotificationToUser(fcmToken, mission);
        }
    }

    @Scheduled(cron = "0 10 19 * * ?")  // 매일 오후 2시에 실행
    public void sendNotificationAtSevenPM() throws FirebaseMessagingException {
        // 모든 유저의 FCM 토큰을 조회, NUll은 제외
        List<String> fcmTokens = memberRepository.findAllFcmTokens();

        Mission mission = missionService.getRanomMission();

        // 모든 유저에게 알림 발송
        for (String fcmToken : fcmTokens) {
            System.out.println("fcmToken = " + fcmToken);
            sendNotificationToUser(fcmToken, mission);
        }
    }

    /**
     * 친구 초대 받았을때
     */
    public MemberNotificationStatusDto sendFriendNotificationToInvitedMember(Member fromMember, Member toMember) {
        String fcmToken = toMember.getFcmToken();
        if(fcmToken == null){
            return MemberNotificationStatusDto.builder()
                    .memberId(toMember.getId())
                    .profileName(toMember.getProfileName())
                    .fcmToken(fcmToken)
                    .isNotificationSuccess(false)
                    .errorMessage("Fcm 토큰이 Null 입니다")
                    .build();
        }
        String randomUUID = getRandomUUID();

        Map<String, String> data = new HashMap<>();
        data.put("title", "친구 요청을 받았을때");
        data.put("body", fromMember.getProfileName() + "님이 당신과 친구가 되고 싶어해요.");
        data.put("id", randomUUID);
        data.put("path", "MyFriend?req=req");
        data.put("imageUrl", fromMember.getPhotoUrl());

        Message message = Message.builder()
                .setToken(fcmToken)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .build();

        boolean isNotificationSuccess = true;
        String errorMessage = null;

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            isNotificationSuccess = false;
            errorMessage = e.getMessage();
        }

        return MemberNotificationStatusDto.builder()
                .memberId(toMember.getId())
                .profileName(toMember.getProfileName())
                .fcmToken(fcmToken)
                .isNotificationSuccess(isNotificationSuccess)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 다이어리(팀) 초대 받았을때
     */
    public List<DiaryInviteResponseDto> sendDiaryNotificationToInvitedMembers(Member fromMember, List<Member> invitedMembers, Diary diary) {
        List<DiaryInviteResponseDto> responseDtos = new ArrayList<>();

        for (Member member : invitedMembers) {
            String fcmToken = member.getFcmToken();
            if(fcmToken == null){
                responseDtos.add(DiaryInviteResponseDto.builder()
                        .memberId(member.getId())
                        .profileName(member.getProfileName())
                        .photoUrl(member.getPhotoUrl())
                        .memberNotificationStatusDto(MemberNotificationStatusDto.builder()
                                .profileName(member.getProfileName())
                                .memberId(member.getId())
                                .fcmToken(fcmToken)
                                .isNotificationSuccess(false)
                                .errorMessage("Fcm 토큰이 Null 입니다")
                                .build())
                        .build());

                continue;
            }
            String randomUUID = getRandomUUID();

            Map<String, String> data = new HashMap<>();
            data.put("title", "다이어리(팀) 초대 받았을때");
            data.put("body", fromMember.getProfileName() + "님이 " + diary.getTitle() + " 모임에 당신을 초대했어요.");
            data.put("id", randomUUID);
            data.put("path", "MyTeamRequest");
            data.put("imageUrl", fromMember.getPhotoUrl());

            boolean isNotificationSuccess = true;
            String errorMessage = null;
            // FCM 토큰이 있는 경우 알림 전송 시도
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();
            try {
                FirebaseMessaging.getInstance().send(message);
                isNotificationSuccess = true;  // 전송 성공
            } catch (Exception e) {
                isNotificationSuccess = false; // 전송 실패
                errorMessage = e.getMessage();
            }

            responseDtos.add(DiaryInviteResponseDto.builder()
                    .memberId(member.getId())
                    .profileName(member.getProfileName())
                    .photoUrl(member.getPhotoUrl())
                    .memberNotificationStatusDto(MemberNotificationStatusDto.builder()
                            .profileName(member.getProfileName())
                            .memberId(member.getId())
                            .fcmToken(fcmToken)
                            .isNotificationSuccess(isNotificationSuccess)
                            .errorMessage(errorMessage)
                            .build())
                    .build());
        }

        return responseDtos;
    }

    /**
     * 누가 새로운 글을 썼을때
     */
    public List<MemberNotificationStatusDto> sendNewBoardNotificationToDiaryMember(Member me, List<Member> members, Diary diary) {
        List<MemberNotificationStatusDto> responseDtos = new ArrayList<>();
        Map<Member, LocalDateTime> recievedAtMap = diaryMemberCustomRepository.findRecievedAtByDiaryAndMembers(diary, members);

        for (Member member : members) {
            String randomUUID = getRandomUUID();
            String fcmToken = member.getFcmToken();

            if(fcmToken == null){
                responseDtos.add(MemberNotificationStatusDto.builder()
                        .memberId(member.getId())
                        .profileName(member.getProfileName())
                        .fcmToken(member.getFcmToken())
                        .isNotificationSuccess(false)
                        .errorMessage("Fcm 토큰이 Null 입니다")
                        .build());
            }

            Map<String, String> data = new HashMap<>();

            data.put("id", randomUUID);
            data.put("path", "Team?teamId=" + diary.getId());
            data.put("imageUrl", me.getPhotoUrl());
            data.put("title", "누가 새로운 글을 썼을때");
            data.put("body", me.getProfileName() + "님이 " + diary.getTitle() + " 모임에 새로운 일상을 등록했어요.");

            LocalDateTime recievedAt = recievedAtMap.get(member);
            try {
                Map<String, Object> teamData = Map.of(
                        "targetDate", LocalDate.now(),
                        "id", diary.getId(),
                        "title", diary.getTitle(),
                        "createdAt", diary.getCreatedAt(),
                        "receivedAt", recievedAt
                );
                data.put("team", objectMapper.writeValueAsString(teamData));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("팀 에러 = " + e.getMessage());
            }

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            boolean isNotificationSuccess = true;
            String errorMessage = null;

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                isNotificationSuccess = false;
                errorMessage = e.getMessage();
            }

            responseDtos.add(MemberNotificationStatusDto.builder()
                    .memberId(member.getId())
                    .profileName(member.getProfileName())
                    .fcmToken(member.getFcmToken())
                    .isNotificationSuccess(isNotificationSuccess)
                    .errorMessage(errorMessage)
                    .build());
        }

        return responseDtos;
    }

    private String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
