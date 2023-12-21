package com.example.demo.service.service;

import com.example.demo.model.dto.UserLoginResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AvatarService {
    UserLoginResponseDTO updateAvatar(String memberId, MultipartFile[] files) throws Exception;
    void deleteAllAvatarByMemberId(Long memberId);
    String saveStaterImage(Long memberId);

}
