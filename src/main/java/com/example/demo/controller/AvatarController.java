package com.example.demo.controller;


import com.example.demo.common.ResponseObject;
import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.service.iml.AvatarServiceIml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/avatar")
@Slf4j
public class AvatarController {

    private final AvatarServiceIml avatarServiceIml;
    private final String imageUploadDirectory = "C:\\Users\\LG CNS\\Downloads\\Images";

    @Autowired
    public AvatarController(AvatarServiceIml avatarServiceIml) {
        this.avatarServiceIml = avatarServiceIml;
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path filePath = Paths.get(imageUploadDirectory).resolve(imageName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Adjust the media type based on your image type
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            // Handle exceptions appropriately
            return ResponseEntity.status(500).build();
        }
    }


    @RequestMapping("/change/{userId}")
    public ResponseEntity<ResponseObject> changeAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile[] files){
        try {
            UserLoginResponseDTO userLoginResponseDTO = avatarServiceIml.updateAvatar(userId, files);
            return ResponseEntity.ok().body(
                    new ResponseObject("success", "Avatar(s) changed successfully", userLoginResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }
}
