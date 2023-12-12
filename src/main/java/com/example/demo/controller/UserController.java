package com.example.demo.controller;

import com.example.demo.common.ResponseObject;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllUsers() {
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
        return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDTOList), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createUser(@RequestBody UserEntity userEntity){
        try {
            log.debug("create user: {}", userEntity.getId());
            String hashedPassword = passwordEncoder.encode(userEntity.getPassword());
            userEntity.setRole(Role.MEMBER);
            userEntity.setPassword(hashedPassword);
            userRepository.save(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("success", "User created successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody UserEntity loginUser) {
        try {
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
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject("success", "Login successful", userResponseDTO)
                    );
                } else {
                    // Passwords do not match
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            new ResponseObject("error", "Login failed: Incorrect password", null)
                    );
                }
            } else {
                // User not found
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("error", "Login failed: User not found", null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable Long userId, @RequestBody UserEntity updatedUser) {
        try {
            Optional<UserEntity> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                UserEntity existingUser = optionalUser.get();

                // Update fields based on your requirements
                existingUser.setName(updatedUser.getName());
                existingUser.setMobilePhone(updatedUser.getMobilePhone());
                existingUser.setEmail(updatedUser.getEmail());

                // Save the updated user
                userRepository.save(existingUser);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("success", "User updated successfully", null)
                );
            } else {
                // User not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("error", "User not found", null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable Long userId) {
        try {
            Optional<UserEntity> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                userRepository.deleteById(userId);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("success", "User deleted successfully", null)
                );
            } else {
                // User not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("error", "User not found", null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }

}
