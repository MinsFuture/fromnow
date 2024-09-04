package com.knu.fromnow.api.domain.photo.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import com.knu.fromnow.api.domain.photo.repository.BoardPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardPhotoService {

    private final BoardPhotoRepository boardPhotoRepository;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    private final Storage storage;

    public void uploadToBoardPhotos(MultipartFile[] files, Board board){
        for (MultipartFile file : files) {
            String photoUrl = uploadImageToGcs(file);
            BoardPhoto photo = BoardPhoto.builder()
                    .board(board)
                    .photoUrl(photoUrl)
                    .build();

            board.getPhotoList().add(photo);
            boardPhotoRepository.save(photo);
        }
    }

    public String uploadImageToGcs(MultipartFile file){
        if(file.isEmpty()){
            Random random = new Random();
            int number = random.nextInt(4) + 1;

            return "https://storage.googleapis.com/fromnow-bucket/basic_image_00" + number + ".png";
        }

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
