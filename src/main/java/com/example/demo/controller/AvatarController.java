package com.example.demo.controller;


import com.example.demo.common.ResponseObject;
import com.example.demo.entity.Avatar;
import com.example.demo.repository.AvatarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/avatar")
@Slf4j
public class AvatarController {

    private final AvatarRepository avatarRepository;
    private final ResourceLoader resourceLoader;
    @Autowired
    public AvatarController(AvatarRepository avatarRepository, ResourceLoader resourceLoader) {
        this.avatarRepository = avatarRepository;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Resource resource = resourceLoader.getResource("classpath:/images/" + imageName);
            log.info("Images {}", resource.getURL());

            if (resource.exists()) {
                System.out.println(resource);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                // Handle the case when the image file does not exist
                System.out.println("not exits");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle the exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @RequestMapping("/change/{userId}")
    public ResponseEntity<ResponseObject> changeAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile[] files){
        try {
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
            // Save the image URLs to your database or perform any necessary operations
            for (String imageUrl : imageUrls) {
                Avatar avatar = new Avatar();

                System.out.println(userId);
                System.out.println(imageUrl);
                avatar.setUserId(userId);
                avatar.setUrl(imageUrl);
                avatarRepository.save(avatar);
            }
            return ResponseEntity.ok().body(
                    new ResponseObject("success", "Avatar(s) changed successfully", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }
}
