package com.knu.fromnow.api.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.json.url}")
    private String firebaseFileUrl;

    @PostConstruct
    public void firestore() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream(firebaseFileUrl);
        //다운받은 비공개 키 이름

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://project-test-383014-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

}