package com.knu.fromnow.api.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class StorageConfig {

    @Bean
    public Storage storage() throws IOException {

        // 파일 시스템 경로에서 직접 파일을 읽기
        FileInputStream inputStream = new FileInputStream("/home/helloaway214/bucket/sunny-wavelet-429609-t9-5d820b98637e.json");

        // GoogleCredentials 객체 생성
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

        // 프로젝트 ID 설정
        String projectId = "sunny-wavelet-429609-t9";

        // Storage 객체 생성 및 반환
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
