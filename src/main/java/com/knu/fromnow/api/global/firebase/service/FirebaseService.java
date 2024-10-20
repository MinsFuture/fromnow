package com.knu.fromnow.api.global.firebase.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.knu.fromnow.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final MemberRepository memberRepository;

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "fromnow-34d51-firebase-adminsdk-fiy84-7b40c25669.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    // 유저별 알림 전송 메서드
    private void sendNotificationToUser(String fcmToken) throws IOException, FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("매일 알림")
                        .setBody("오후 2시 알림입니다!")
                        .build())
                .build();

        // Firebase로 알림 전송
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }

    @Scheduled(cron = "0 0 14 * * ?")  // 매일 오후 2시에 실행
    public void sendNotificationAtTwoPM() throws IOException, FirebaseMessagingException {
        // AccessToken을 가져오는 로직
        String accessToken = getAccessToken();
        System.out.println("accessToken = " + accessToken);

        // 모든 유저의 FCM 토큰을 조회
        List<String> fcmTokens = memberRepository.findAllFcmTokens();

        // 모든 유저에게 알림 발송
        for (String fcmToken : fcmTokens) {
            sendNotificationToUser(fcmToken);
        }
    }


}
