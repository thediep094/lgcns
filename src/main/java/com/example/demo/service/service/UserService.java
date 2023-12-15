package com.example.demo.service.service;

import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAllUser();
    UserResponseDTO saveUser(UserEntity userEntity) throws Exception;
    Page<UserResponseDTO> findAllUserFilter(
            Long id,
            String name,
            String phoneNumber,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );
    List<UserResponseDTO> findAllUserFilterExport(
            Long id,
            String name,
            String phoneNumber,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    UserResponseDTO findUserById(Long id, UserEntity updatedUser) throws Exception;
    UserResponseDTO deleteUserById(Long id) throws Exception;

    UserResponseDTO loginUser(UserEntity userEntity) throws Exception;
}
