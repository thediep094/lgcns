package com.example.demo.service.iml;

import com.example.demo.model.entity.Avatar;
import com.example.demo.repository.AvatarRepository;
import com.example.demo.service.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AvatarServiceIml implements AvatarService {
    private final AvatarRepository avatarRepository;
    @Autowired
    public AvatarServiceIml(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    public void deleteAllAvatarByUserId(Long userId) {
        log.debug("Request to delete all avatar by userId: {}", userId);
        List<Avatar> avatars = avatarRepository.findAllByUserId(userId);
        avatarRepository.deleteAll(avatars);
    }

    public String findUrlAvatarUser(Long userId) {
        log.debug("Request to find one avatar by userId: {}", userId);
        Avatar avatar = avatarRepository.findTopByUserId(userId);
        return avatar.getUrl();
    }

    public String saveStaterImage(Long userId) {
        log.debug("Request to save one avatar for userId stater: {}", userId);
        Avatar avatar = new Avatar();
        avatar.setUserId(userId);
        avatar.setUrl("user.jpg");
        Avatar responseAvatar = avatarRepository.save(avatar);
        return responseAvatar.getUrl();
    }

    @Override
    public List<String> updateAvatar(Long userId, MultipartFile[] files) {
        String imageUploadDirectory = "src/main/resources/images";
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            try {
                Path directoryPath = Paths.get(imageUploadDirectory);
                String filePath = directoryPath.resolve(filename).toAbsolutePath().toString();

                // Save the file to the specified directory
                file.transferTo(new File(filePath));

                imageUrls.add(filename);
                log.info("Upload to upload image: {}", filename);
            } catch (IOException e) {
                // Handle the exception appropriately
                log.error("Failed to upload image: {}", e.getMessage());
            }
        }
        deleteAllAvatarByUserId(userId);
        // Save the image URLs to your database or perform any necessary operations
        for (String imageUrl : imageUrls) {
            Avatar avatar = new Avatar();
            log.debug("Userid: {}", userId);
            log.debug("imageUrl: {}", imageUrl);
            avatar.setUserId(userId);
            avatar.setUrl(imageUrl);
            avatarRepository.save(avatar);
        }
        return imageUrls;
    }
}
