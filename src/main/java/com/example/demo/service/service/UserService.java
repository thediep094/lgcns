package com.example.demo.service.service;

import com.example.demo.model.dto.PasswordChangeDTO;
import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAllUser();
    UserResponseDTO saveUser(UserEntity userEntity) throws Exception;
    Page<UserResponseDTO> findAllUserFilter(
            String id,
            String name,
            String phoneNumber,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    Boolean changePassword(String userId, PasswordChangeDTO passwordChangeDTO) throws Exception;
    List<UserResponseDTO> findAllUserFilterExport(
            String id,
            String name,
            String phoneNumber,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    UserLoginResponseDTO findUserById(String userId, String findUserId) throws Exception;
    UserLoginResponseDTO findUserByIdAndUpdate(String id, UserEntity updatedUser) throws Exception;
    UserResponseDTO deleteUserById(String id, String requestUserId) throws Exception;

    UserLoginResponseDTO loginUser(UserEntity userEntity) throws Exception;
}
