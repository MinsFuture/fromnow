package com.knu.fromnow.api.global.azure.controller;

import com.knu.fromnow.api.global.azure.service.AzureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/azure/test")
public class AzureBlobStorageController {

    private final AzureBlobStorageService azureBlobStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String containerName = "testcontainer"; // 원하는 컨테이너 이름
        String fileName = file.getOriginalFilename();

        // Blob Storage에 파일 업로드
        String fileUrl = azureBlobStorageService.uploadFile(
                containerName,
                fileName,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
        );

        return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
    }
}
