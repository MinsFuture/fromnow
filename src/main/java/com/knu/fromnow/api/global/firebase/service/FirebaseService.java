package com.knu.fromnow.api.global.firebase.service;


import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FirebaseService {


    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "fromnow-34d51-firebase-adminsdk-fiy84-7b40c25669.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Scheduled(cron = "0 11 15 * * ?")  // 매일 오후 2시에 실행
    public void sendNotificationAtTwoPM() throws IOException {
        String accessToken = getAccessToken();
        System.out.println("accessToken = " + accessToken);
    }


}
