package com.knu.fromnow.api.global.azure.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String connectionString;

    @Value("${spring.cloud.azure.storage.blob.endpoint}")
    private String endPoint;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .endpoint(endPoint)
                .buildClient();
    }
}
