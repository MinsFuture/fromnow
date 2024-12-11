package com.knu.fromnow.api.domain.photo.service;

import com.knu.fromnow.api.domain.board.entity.Board;
import com.knu.fromnow.api.domain.photo.entity.BoardPhoto;
import com.knu.fromnow.api.domain.photo.repository.BoardPhotoRepository;
import com.knu.fromnow.api.global.azure.service.AzureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardPhotoService {

    private final BoardPhotoRepository boardPhotoRepository;
    private final AzureBlobStorageService azureBlobStorageService;

    public void uploadToBoardPhotos(MultipartFile file, Board board) throws IOException {
            String photoUrl = azureBlobStorageService.uploadImageToAzure(file);
            BoardPhoto photo = BoardPhoto.builder()
                    .board(board)
                    .photoUrl(photoUrl)
                    .build();

            board.uploadBoardPhoto(photo);
            boardPhotoRepository.save(photo);
    }

    public String getRandomImageFromAzure(){
        Random random = new Random();
        int number = random.nextInt(4) + 1;

        return "https://fromnowstorage.blob.core.windows.net/fromnowcontainer/basic_image_00" + number + ".png";
    }


}
