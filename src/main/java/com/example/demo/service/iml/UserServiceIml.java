package com.example.demo.service.iml;

import com.example.demo.model.dto.PasswordChangeDTO;
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

    private static boolean containsLetters(String s) {
        return s.matches(".*[a-zA-Z].*");
    }

    private static boolean containsNumbers(String s) {
        return s.matches(".*\\d.*");
    }

    private static boolean containsSpecialCharacters(String s) {
        return s.matches(".*[^a-zA-Z0-9].*");
    }

    private static Integer countContain(String s) {
        Integer count = 0;
        if(containsLetters(s)) {
            count += 1;
        }

        if(containsNumbers(s)) {
            count += 1;
        }

        if(containsSpecialCharacters(s)) {
            count += 1;
        }

        return count;
    }

    private void validatePassword(String password) throws Exception {
        // Password must have at least 8 characters
        if (password.length() < 8) {
            throw new Exception("Your password not formatted correctly: Password must have at least 8 characters");
        }

        if(countContain(password) < 2) {
            throw new Exception("Your password not formatted correctly: Password must have at least 2 combinations: letters, numbers, or special characters");
        }

        // Password must have at least 10 characters if have 2 combinations, or 3 combinations need at least 8 characrers
        if (password.length() < 10 && countContain(password) == 2) {
            throw new Exception("Your password not formatted correctly: Password must have at least 10 characters if 2 combinations: letters, numbers, or special characters");
        }

        if (password.length() < 8 && countContain(password) == 3) {
            throw new Exception("Your password not formatted correctly: Password must have at least 8 characters if 3 combinations: letters, numbers, or special characters");
        }

        // Special characters allowed are @#$%^&*
        if (!password.matches("^[a-zA-Z0-9@#$%^&*]+$")) {
            throw new Exception("Your password not formatted correctly: Password can only contain letters, numbers, and the special characters @#$%^&*");
        }

        // Consecutive numbers must not be more than 3 characters
        if (password.matches(".*\\d{4,}.*")) {
            throw new Exception("Your password not formatted correctly: Consecutive numbers must not be more than 3 characters");
        }
    }

    public void checkAdmin(String id) throws Exception {
        if(!userRepository.findByUserId(id).get().getRole().equals(Role.ADMIN)) {
            throw new Exception("You are not admin");
        }
    }

    public void checkAdminByMemberId(Long memberId) throws Exception {
        if(!userRepository.findByMemberId(memberId).get().getRole().equals(Role.ADMIN)) {
            throw new Exception("You are not admin");
        }
    }

    public UserLoginResponseDTO findUserById(String userId, String findUserId) throws Exception{
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        Optional<UserEntity> optional2User = userRepository.findByUserId(findUserId);
        if (optionalUser.isPresent() && optional2User.isPresent()) {
            UserEntity adminUser = optionalUser.get();
            UserEntity findUser = optional2User.get();
            if (adminUser.getRole().equals(Role.ADMIN)) {
                String avatar = avatarServiceIml.findUrlAvatarUser(findUser.getMemberId());
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(
                        findUser.getMemberId(),
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
                        userEntity.getMemberId(),
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

    public UserLoginResponseDTO findUserByIdAndUpdate(String userId, UserEntity updatedUser) throws Exception{
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
                String avatar = avatarServiceIml.findUrlAvatarUser(existingUser.getMemberId());
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(updatedUser.getMemberId(), userId, updatedUser.getName(), updatedUser.getMobilePhone(), updatedUser.getEmail(), existingUser.getRole(), existingUser.getDate(), avatar);
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
    public UserResponseDTO deleteUserById(String userId, String requestUserId) throws Exception {
        checkAdmin(requestUserId);
        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteByUserId(userId);
            UserEntity existingUser = optionalUser.get();
            avatarServiceIml.deleteAllAvatarByMemberId(optionalUser.get().getMemberId());
            return new UserResponseDTO(existingUser.getMemberId(), existingUser.getUserId(), existingUser.getName(), existingUser.getMobilePhone(), existingUser.getEmail(), existingUser.getRole(), existingUser.getDate());
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

        if (!userEntity.getMobilePhone().matches("^[0-9]+$")) {
            throw new Exception("Mobile phone number can only contain numbers");
        }

        validatePassword(userEntity.getPassword());

        String hashedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setRole(Role.MEMBER);
        userEntity.setPassword(hashedPassword);
        userEntity.setDate(new java.sql.Date(System.currentTimeMillis()));
        userRepository.save(userEntity);
        String avatar = avatarServiceIml.saveStaterImage(userEntity.getMemberId());
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(userEntity.getMemberId(),userEntity.getUserId(), userEntity.getName(), userEntity.getMobilePhone(), userEntity.getEmail(),userEntity.getRole(), userEntity.getDate(), avatar);
        return userLoginResponseDTO;
    }

    @Override
    public UserLoginResponseDTO loginUser(UserEntity loginUser) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(loginUser.getUserId());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
                String avatar = avatarServiceIml.findUrlAvatarUser(optionalUser.get().getMemberId());
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(
                        user.getMemberId(),
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
            String userId,
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
            specification = specification.and(UserSpecifications.userIdPartialMatch(userId));
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
                        userEntity.getMemberId(),
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
            String userId,
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

            specification = specification.and(UserSpecifications.userIdPartialMatch(userId));
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
                        userEntity.getMemberId(),
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

    @Override
    public Boolean changePassword(String userId, PasswordChangeDTO passwordChangeDTO) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(passwordChangeDTO.getUserId());
        if(!userId.equals(passwordChangeDTO.getUserId())) {
            throw new Exception("It not your account");
        }
        validatePassword(passwordChangeDTO.getNewPassword());
        if(optionalUser.isPresent()) {
            if (passwordEncoder.matches(passwordChangeDTO.getOldPassword(), optionalUser.get().getPassword())){
                String hashedPassword = passwordEncoder.encode(passwordChangeDTO.getNewPassword());
                UserEntity existingUser = optionalUser.get();
                existingUser.setPassword(hashedPassword);
                userRepository.save(existingUser);
                return true;
            } else {
                throw new Exception("Old password not match");
            }
        } else {
            throw new Exception("User not found");
        }
    }
}
