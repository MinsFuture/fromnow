package com.knu.fromnow.api.domain.photo.service;

import com.knu.fromnow.api.domain.diary.entity.Diary;
import com.knu.fromnow.api.domain.member.entity.Member;
import com.knu.fromnow.api.domain.photo.entity.Photo;
import com.knu.fromnow.api.domain.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoService {

    private final PhotoRepository photoRepository;

    public String createPhotoName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        return uniqueFilename;
    }

    public void uploadPhoto(MultipartFile[] files, Diary diary){
        List<Photo> photoList = diary.getPhotoList();

        for (MultipartFile file : files) {
            String uniqueFileName = createPhotoName(file);
            String photoUrl = createPhotoUrl(uniqueFileName);

            Photo photo = Photo.builder()
                    .photoUrl(photoUrl)
                    .build();
            photoRepository.save(photo);
        }

        // GCP 업로드

        //




    }

    public String createPhotoUrl(String uniqueFileName){
        //

        return uniqueFileName + "test";

    }

}
