package com.example.demo.service.service;

import com.example.demo.model.dto.UserLoginResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService {
    UserLoginResponseDTO updateAvatar(String userId, MultipartFile[] files) throws Exception;
    void deleteAllAvatarByUserId(String userId);
    String saveStaterImage(String userId);

}
