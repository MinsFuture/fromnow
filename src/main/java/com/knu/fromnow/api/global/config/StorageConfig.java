package com.knu.fromnow.api.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Configuration
public class StorageConfig {

    @Bean
    public Storage storage() throws IOException {

        // ClassPathResource를 통해 리소스의 InputStream을 얻는다.
        ClassPathResource resource = new ClassPathResource("sunny-wavelet-429609-t9-5d820b98637e.json");
        InputStream inputStream = resource.getInputStream();

        // 임시 파일을 생성한다.
        Path tempFilePath = Files.createTempFile("sunny-wavelet-429609-t9-5d820b98637e", ".json");
        File tempFile = tempFilePath.toFile();

        try {
            // InputStream의 데이터를 임시 파일에 복사한다.
            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            // InputStream을 닫는다.
            inputStream.close();
        }

        // JSON 파일을 통해 GoogleCredentials를 생성한다.
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(tempFilePath));
        String projectId = "sunny-wavelet-429609-t9";

        // 임시 파일 삭제 (사용 후 삭제해도 좋지만, 재사용할 경우에는 주석 처리)
        tempFile.deleteOnExit();

        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
