package com.example.demo.service.iml;

import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.service.UserService;
import com.example.demo.specification.UserSpecifications;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    private final AvatarServiceIml avatarServiceIml;

    @Autowired
    public UserServiceIml(UserRepository userRepository, PasswordEncoder passwordEncoder,  AvatarServiceIml avatarServiceIml) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.avatarServiceIml = avatarServiceIml;
    }

    private void validatePassword(String password) throws Exception {
        // Password must have at least 8 characters
        if (password.length() < 8) {
            throw new Exception("Password must have at least 8 characters");
        }

        // Password must have at least 2 combinations if length is less than 8, or 3 combinations if length is less than 10
        if (password.length() < 8 && !password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&*]).+$")) {
            throw new Exception("Password must have at least 2 combinations: letters, numbers, or special characters");
        }

        if (password.length() < 10 && !password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&*]).+$")) {
            throw new Exception("Password must have at least 3 combinations: letters, numbers, or special characters");
        }

        // Special characters allowed are @#$%^&*
        if (!password.matches("^[a-zA-Z0-9@#$%^&*]+$")) {
            throw new Exception("Password can only contain letters, numbers, and the special characters @#$%^&*");
        }

        // Consecutive numbers must not be more than 3 characters
        if (password.matches(".*\\d{4,}.*")) {
            throw new Exception("Consecutive numbers must not be more than 3 characters");
        }
    }

    public UserLoginResponseDTO findUserById(Long userId, Long findUserId) throws Exception{
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        Optional<UserEntity> optional2User = userRepository.findByUserId(findUserId);
        if (optionalUser.isPresent() && optional2User.isPresent()) {
            UserEntity adminUser = optionalUser.get();
            UserEntity findUser = optional2User.get();
            if (adminUser.getRole().equals(Role.ADMIN)) {
                String avatar = avatarServiceIml.findUrlAvatarUser(findUserId);
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(
                        findUser.getUserId(),
                        findUser.getName(),
                        findUser.getMobilePhone(),
                        findUser.getEmail(),
                        findUser.getRole(),
                        findUser.getDate(),
                        avatar
                );
                return userLoginResponseDTO;
            } else {
                // Passwords do not match
                throw new Exception("Get user failed: You are not admin");
            }
        } else {
            // User not found
            throw new Exception("Login failed: User not found");
        }
    }

    public List<UserResponseDTO> findAllUser() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponseDTO> userDTOList = userEntities.stream()
                .map(userEntity -> new UserResponseDTO(
                        userEntity.getUserId(),
                        userEntity.getName(),
                        userEntity.getMobilePhone(),
                        userEntity.getEmail(),
                        userEntity.getRole(), userEntity.getDate()
                        // Add any other fields you need, excluding the password
                ))
                .collect(Collectors.toList());
        return userDTOList;
    }

    public UserLoginResponseDTO findUserByIdAndUpdate(Long userId, UserEntity updatedUser) throws Exception{
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        Optional<UserEntity> optional2User = userRepository.findByUserId(updatedUser.getUserId());
        if (optionalUser.isPresent() && optional2User.isPresent()) {
            UserEntity fromUser = optionalUser.get();
            UserEntity existingUser = optional2User.get();
            // Update fields based on your requirements
            if(fromUser.getRole().equals(Role.ADMIN) || existingUser.getUserId().equals(userId) ) {

                existingUser.setRole(updatedUser.getRole());
                existingUser.setName(updatedUser.getName());
                existingUser.setMobilePhone(updatedUser.getMobilePhone());
                existingUser.setEmail(updatedUser.getEmail());
                // Save the updated user
                userRepository.save(existingUser);
                String avatar = avatarServiceIml.findUrlAvatarUser(userId);
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(userId, updatedUser.getName(), updatedUser.getMobilePhone(), updatedUser.getEmail(), existingUser.getRole(), existingUser.getDate(), avatar);
                return userLoginResponseDTO;
            } else {
                throw new Exception("You are not admin or not your account to update!");
            }
        } else {
            // User not found
           throw new Exception("User not found");
        }
    }
    @Transactional
    public UserResponseDTO deleteUserById(Long userId) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            userRepository.deleteByUserId(userId);
            UserEntity existingUser = optionalUser.get();
            avatarServiceIml.deleteAllAvatarByUserId(userId);
            return new UserResponseDTO(existingUser.getUserId(), existingUser.getName(), existingUser.getMobilePhone(), existingUser.getEmail(), existingUser.getRole(), existingUser.getDate());
        } else {
            // User not found
            throw new Exception("User nor found");
        }
    }

    @Transactional
    public UserLoginResponseDTO saveUser(UserEntity userEntity) throws Exception{
        log.debug("create user: {}", userEntity.getUserId());

        Optional<UserEntity> optionalUser = userRepository.findByUserId(userEntity.getUserId());
        if(optionalUser.isPresent()) {
            throw new Exception("Already have this user");
        }

        // Check if the name is a single word containing only letters
        if (!userEntity.getName().matches("^[a-zA-Z ]+$")) {
            throw new Exception("Name must be a single word containing only letters");
        }

        if(String.valueOf(userEntity.getUserId()).length() < 4) {
            throw new Exception("Id must be at least 4 digits long");
        }

        if (!userEntity.getMobilePhone().matches("\\d+")) {
            throw new Exception("Mobile phone number can only contain numbers");
        }

        validatePassword(userEntity.getPassword());

        String hashedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setRole(Role.MEMBER);
        userEntity.setPassword(hashedPassword);
        userEntity.setDate(new java.sql.Date(System.currentTimeMillis()));
        userRepository.save(userEntity);
        String avatar = avatarServiceIml.saveStaterImage(userEntity.getUserId());
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(userEntity.getUserId(), userEntity.getName(), userEntity.getMobilePhone(), userEntity.getEmail(),userEntity.getRole(), userEntity.getDate(), avatar);
        return userLoginResponseDTO;
    }

    @Override
    public UserLoginResponseDTO loginUser(UserEntity loginUser) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(loginUser.getUserId());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
                String avatar = avatarServiceIml.findUrlAvatarUser(loginUser.getUserId());
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(
                        user.getUserId(),
                        user.getName(),
                        user.getMobilePhone(),
                        user.getEmail(),
                        user.getRole(),
                        user.getDate(),
                        avatar
                );
                return userLoginResponseDTO;
            } else {
                // Passwords do not match
                throw new Exception("Login failed: Incorrect password");
            }
        } else {
            // User not found
            throw new Exception("Login failed: User not found");
        }
    }

    public Page<UserResponseDTO> findAllUserFilter(
            Long userId,
            String name,
            String mobilePhone,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        // Build a specification based on the filter criteria
        Specification<UserEntity> specification = Specification.where(null);

        if (userId != null) {
            specification = specification.and(UserSpecifications.userIdPartialMatch(String.valueOf(userId)));
        }

        if (name != null && !name.isEmpty()) {
            specification = specification.and(UserSpecifications.nameLike(name));
        }

        if (mobilePhone != null && !mobilePhone.isEmpty()) {
            specification = specification.and(UserSpecifications.mobilePhoneLike(mobilePhone));
        }

        if (fromDate != null && toDate != null) {
            specification = specification.and(UserSpecifications.dateBetween(fromDate, toDate));
        }

        Pageable pageable;
        if(sortBy == null) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
            pageable = PageRequest.of(page, size, sort);
        }

        Page<UserEntity> userEntitiesPage = userRepository.findAll(specification, pageable);

        List<UserResponseDTO> userDTOList = userEntitiesPage.getContent().stream()
                .map(userEntity -> new UserResponseDTO(
                        userEntity.getUserId(),
                        userEntity.getName(),
                        userEntity.getMobilePhone(),
                        userEntity.getEmail(),
                        userEntity.getRole(),
                        userEntity.getDate()
                        // Add any other fields you need, excluding the password
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOList, pageable, userEntitiesPage.getTotalElements());
    }


    public List<UserResponseDTO> findAllUserFilterExport(
            Long userId,
            String name,
            String mobilePhone,
            Date fromDate,
            Date toDate,
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        // Build a specification based on the filter criteria
        Specification<UserEntity> specification = Specification.where(null);

        if (userId != null) {

            specification = specification.and(UserSpecifications.userIdPartialMatch(String.valueOf(userId)));
        }

        if (name != null && !name.isEmpty()) {
            specification = specification.and(UserSpecifications.nameLike(name));
        }

        if (mobilePhone != null && !mobilePhone.isEmpty()) {
            specification = specification.and(UserSpecifications.mobilePhoneLike(mobilePhone));
        }

        if (fromDate != null && toDate != null) {
            specification = specification.and(UserSpecifications.dateBetween(fromDate, toDate));
        }
        List<UserEntity> userEntities;
        if(sortBy == null) {
            userEntities = userRepository.findAll(specification);
        } else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
            userEntities = userRepository.findAll(specification, sort);
        }

        List<UserResponseDTO> userDTOList = userEntities.stream()
                .map(userEntity -> new UserResponseDTO(
                        userEntity.getUserId(),
                        userEntity.getName(),
                        userEntity.getMobilePhone(),
                        userEntity.getEmail(),
                        userEntity.getRole(),
                        userEntity.getDate()
                        // Add any other fields you need, excluding the password
                ))
                .collect(Collectors.toList());
        return userDTOList;
    }


}
