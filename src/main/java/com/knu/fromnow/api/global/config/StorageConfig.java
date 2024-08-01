package com.knu.fromnow.api.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class StorageConfig {

    @Value("${google.cloud.credentials.path}")
    private String credentialsPath;

    @Bean
    public Storage storage() throws IOException {
        try {
            FileInputStream inputStream = new FileInputStream(credentialsPath);

            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
            String projectId = "sunny-wavelet-429609-t9";

            return StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found at path: /config/sunny-wavelet-429609-t9-5d820b98637e.json", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file at path: /config/sunny-wavelet-429609-t9-5d820b98637e.json", e);
        }
    }
}
