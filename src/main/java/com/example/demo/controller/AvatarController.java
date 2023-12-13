package com.example.demo.controller;


import com.example.demo.common.ResponseObject;
import com.example.demo.service.iml.AvatarServiceIml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.util.List;

@RestController
@RequestMapping("/avatar")
@Slf4j
public class AvatarController {

    private final AvatarServiceIml avatarServiceIml;
    private final ResourceLoader resourceLoader;
    @Autowired
    public AvatarController(AvatarServiceIml avatarServiceIml, ResourceLoader resourceLoader) {
        this.avatarServiceIml = avatarServiceIml;
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
            List<String> imagesUrl = avatarServiceIml.updateAvatar(userId, files);
            return ResponseEntity.ok().body(
                    new ResponseObject("success", "Avatar(s) changed successfully", imagesUrl)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }
}
