package com.example.demo.service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService {
    List<String> updateAvatar(Long userId, MultipartFile[] files) throws Exception;
    void deleteAllAvatarByUserId(Long userId);
    String saveStaterImage(Long userId);

}
