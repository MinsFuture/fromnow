package com.knu.fromnow.api.global.firebase.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.knu.fromnow.api.domain.diary.dto.response.DiaryInviteResponseDto;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.diary.repository.DiaryMemberCustomRepository;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import com.knu.fromnow.api.global.spec.firebase.MemberNotificationStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ObjectMapper objectMapper;

    // 유저별 알림 전송 메서드
    private void sendNotificationToUser(String fcmToken) throws FirebaseMessagingException {
        String randomUUID = getRandomUUID();

        Map<String, String> data = new HashMap<>();
        data.put("id", randomUUID);
        data.put("mission", "true");
        data.put("path", "Camera");

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

    @Scheduled(cron = "0 0 15 * * ?")  // 매일 오후 3시에 실행
    public void sendNotificationAtThreePM() throws FirebaseMessagingException {
        // 모든 유저의 FCM 토큰을 조회, NUll은 제외
        List<String> fcmTokens = memberRepository.findAllFcmTokens();

        // 모든 유저에게 알림 발송
        for (String fcmToken : fcmTokens) {
            sendNotificationToUser(fcmToken);
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
