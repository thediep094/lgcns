package com.example.demo.service.iml;

import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.service.UserService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceIml(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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

    public UserResponseDTO findUserById(Long userId, UserEntity updatedUser) throws Exception{
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();

            // Update fields based on your requirements
            existingUser.setName(updatedUser.getName());
            existingUser.setMobilePhone(updatedUser.getMobilePhone());
            existingUser.setEmail(updatedUser.getEmail());

            // Save the updated user
            userRepository.save(existingUser);
            UserResponseDTO userResponseDTO = new UserResponseDTO(userId, updatedUser.getName(), updatedUser.getMobilePhone(), updatedUser.getEmail(), existingUser.getRole());
           return userResponseDTO;
        } else {
            // User not found
           throw new Exception("User not found");
        }
    }

    public UserResponseDTO deleteUserById(Long userId) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
            UserEntity existingUser = optionalUser.get();

            return new UserResponseDTO(existingUser.getId(), existingUser.getName(), existingUser.getMobilePhone(), existingUser.getEmail(), existingUser.getRole());
        } else {
            // User not found
            throw new Exception("User nor found");
        }
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

    @Override
    public UserResponseDTO loginUser(UserEntity loginUser) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findById(loginUser.getId());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
                UserResponseDTO userResponseDTO = new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getMobilePhone(),
                        user.getEmail(),
                        user.getRole()
                );
                return userResponseDTO;
            } else {
                // Passwords do not match
                throw new Exception("Login failed: Incorrect password");
            }
        } else {
            // User not found
            throw new Exception("Login failed: User not found");
        }
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
