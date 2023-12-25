package com.example.demo.service.iml;

import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.model.entity.Avatar;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.repository.AvatarRepository;
import com.example.demo.repository.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AvatarServiceIml implements AvatarService {
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;
    @Autowired
    public AvatarServiceIml(AvatarRepository avatarRepository, UserRepository userRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void deleteAllAvatarByMemberId(Long memberId) {
        log.debug("Request to delete all avatar by memberId: {}", memberId);
        List<Avatar> avatars = avatarRepository.findAllByMemberId(memberId);
        avatarRepository.deleteAll(avatars);
    }

    public String findUrlAvatarUser(Long memberId) {
        log.debug("Request to find one avatar by memberId: {}", memberId);
        Avatar avatar = avatarRepository.findFirstByMemberId(memberId);
        return avatar.getUrl();
    }

    public String saveStaterImage(Long memberId) {
        log.debug("Request to save one avatar for memberId stater: {}", memberId);
        Avatar avatar = new Avatar();
        avatar.setMemberId(memberId);
        avatar.setUrl("user.jpg");
        Avatar responseAvatar = avatarRepository.save(avatar);
        return responseAvatar.getUrl();
    }

    @Override
    public UserLoginResponseDTO updateAvatar(String userId, MultipartFile[] files) throws Exception{
        String imageUploadDirectory = "C:\\Users\\63200202\\Downloads\\Images";
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

        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        if(optionalUser.isPresent()) {
            deleteAllAvatarByMemberId(optionalUser.get().getMemberId());
            // Save the image URLs to your database or perform any necessary operations
            for (String imageUrl : imageUrls) {
                Avatar avatar = new Avatar();
                log.debug("memberId: {}", optionalUser.get().getMemberId());
                log.debug("imageUrl: {}", imageUrl);
                avatar.setMemberId(optionalUser.get().getMemberId());
                avatar.setUrl(imageUrl);
                avatarRepository.save(avatar);
            }
            UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
            if(optionalUser.isPresent()) {
                UserEntity findUser = optionalUser.get();
                String avatar = this.findUrlAvatarUser(findUser.getMemberId());
                userLoginResponseDTO = new UserLoginResponseDTO(
                        findUser.getUserId(),
                        findUser.getName(),
                        findUser.getMobilePhone(),
                        findUser.getEmail(),
                        findUser.getRole(),
                        findUser.getDate(),
                        avatar
                );
            }

            return userLoginResponseDTO;
        }
    return null;
    }
}
