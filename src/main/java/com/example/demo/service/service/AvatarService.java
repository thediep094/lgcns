package com.example.demo.service.service;

import com.example.demo.model.dto.UserLoginResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService {
    UserLoginResponseDTO updateAvatar(Long userId, MultipartFile[] files) throws Exception;
    void deleteAllAvatarByUserId(Long userId);
    String saveStaterImage(Long userId);

}
