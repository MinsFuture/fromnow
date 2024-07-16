package com.knu.fromnow.api.domain.photo.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.photo.entity.Photo;
import com.knu.fromnow.api.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    private final Storage storage;


    public void uploadPhoto(MultipartFile[] files, Diary diary){
        for (MultipartFile file : files) {
            String photoUrl = uploadImageToGcs(file);
            Photo photo = Photo.builder()
                    .diary(diary)
                    .photoUrl(photoUrl)
                    .build();

            diary.getPhotoList().add(photo);
            photoRepository.save(photo);
        }
    }

    public String uploadImageToGcs(MultipartFile file){
        String uniqueFileName = createPhotoName(file);
        BlobId blobId = BlobId.of(bucketName, uniqueFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try (WriteChannel writer = storage.writer(blobInfo)){
            byte[] photoDatas = file.getBytes();
            writer.write(ByteBuffer.wrap(photoDatas));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return createPhotoUrl(uniqueFileName);
    }


    public String createPhotoName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        return UUID.randomUUID() + "_" + originalFilename;
    }

    public String createPhotoUrl(String uniqueFileName){
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, uniqueFileName);
    }

}
