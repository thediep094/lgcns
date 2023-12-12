package com.example.demo.service;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.specification.UserSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> findAllUser() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponseDTO> userDTOList = userEntities.stream()
                .map(userEntity -> new UserResponseDTO(
                        userEntity.getId(),
                        userEntity.getName(),
                        userEntity.getMobilePhone(),
                        userEntity.getEmail(),
                        userEntity.getRole()
                        // Add any other fields you need, excluding the password
                ))
                .collect(Collectors.toList());
        return userDTOList;
    }

    public UserResponseDTO saveUser(UserEntity userEntity) {
        log.debug("create user: {}", userEntity.getId());
        String hashedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setRole(Role.MEMBER);
        userEntity.setPassword(hashedPassword);
        userEntity.setDate(new java.sql.Date(System.currentTimeMillis()));
        userRepository.save(userEntity);

        UserResponseDTO userResponseDTO = new UserResponseDTO(userEntity.getId(), userEntity.getName(), userEntity.getMobilePhone(), userEntity.getEmail(),userEntity.getRole());
        return userResponseDTO;
    }

    public List<UserResponseDTO> findAllUserFilter(
            Long id,
            String name,
            String phoneNumber,
            Date fromDate,
            Date toDate,
            int page,
            int size
    ){
        // Build a specification based on the filter criteria
        Specification<UserEntity> specification = Specification.where(null);

        if (id != null) {
            specification = specification.and(UserSpecifications.idPartialMatch(String.valueOf(id)));
        }

        if (name != null && !name.isEmpty()) {
            specification = specification.and(UserSpecifications.nameLike(name));
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            specification = specification.and(UserSpecifications.phoneNumberLike(phoneNumber));
        }

        if (fromDate != null && toDate != null) {
            specification = specification.and(UserSpecifications.dateBetween(fromDate, toDate));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userEntitiesPage = userRepository.findAll(specification,pageable);

        List<UserResponseDTO> userDTOList = userEntitiesPage.getContent().stream()
                .map(userEntity -> new UserResponseDTO(
                        userEntity.getId(),
                        userEntity.getName(),
                        userEntity.getMobilePhone(),
                        userEntity.getEmail(),
                        userEntity.getRole()
                        // Add any other fields you need, excluding the password
                ))
                .collect(Collectors.toList());
        return userDTOList;
    }



}
