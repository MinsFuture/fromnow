package com.knu.fromnow.api.global.azure.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AzureBlobStorageService {

    private final BlobServiceClient blobServiceClient;

    public String uploadFile(String containerName, String fileName, InputStream fileInputStream, long fileSize, String contentType) {
        // 컨테이너 생성 또는 가져오기
        var blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }

        // Blob 업로드
        var blobClient = blobContainerClient.getBlobClient(fileName);
        blobClient.upload(fileInputStream, fileSize, true);

        // Content-Type 설정
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        blobClient.setHttpHeaders(headers);

        // 업로드 완료 후 Blob URL 반환
        return blobClient.getBlobUrl();
    }

}
