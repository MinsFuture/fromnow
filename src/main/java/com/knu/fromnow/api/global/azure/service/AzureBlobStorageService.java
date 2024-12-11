package com.knu.fromnow.api.global.azure.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.knu.fromnow.api.domain.photo.service.BoardPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AzureBlobStorageService {

    private final BlobServiceClient blobServiceClient;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    public String uploadImageToAzure(MultipartFile file) throws IOException {
        var blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

        String fileName = createPhotoName(file);
        InputStream fileInputStream = file.getInputStream();
        long fileSize = file.getSize();
        String contentType = file.getContentType();

        // Blob 업로드
        var blobClient = blobContainerClient.getBlobClient(fileName);
        blobClient.upload(fileInputStream, fileSize, true);

        // Content-Type 설정
        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        blobClient.setHttpHeaders(headers);

        // 업로드 완료 후 Blob URL 반환
        return blobClient.getBlobUrl();
    }


    public String createPhotoName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        return UUID.randomUUID() + "_" + originalFilename;
    }

}
