package com.example.demo.controller;

import com.example.demo.common.ResponseObject;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.specification.UserSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

//    Get all user not use filter
    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllUsers() {
        List<UserResponseDTO> userDTOList = userService.findAllUser();
        return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDTOList), HttpStatus.OK);
    }


//    Create user
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createUser(@RequestBody UserEntity userEntity){
        try {
            UserResponseDTO userResponseDTO = userService.saveUser(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("success", "User created successfully", userResponseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );        }
    }

//    Auth login
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

//    Update user data
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

//    Delete user
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

//    Get all user using filter
    @GetMapping("/getUser")
    public ResponseEntity<ResponseObject> getUserByFilter(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<UserResponseDTO> userDTOList = userService.findAllUserFilter(id,name, phoneNumber, fromDate, toDate, page, size);
            return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDTOList), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }


}
