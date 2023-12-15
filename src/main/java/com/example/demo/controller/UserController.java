package com.example.demo.controller;

import com.example.demo.common.ResponseObject;
import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.service.iml.ExportExcelIml;
import com.example.demo.service.iml.UserServiceIml;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserServiceIml userServiceIml;
    private final ExportExcelIml exportExcelIml;
    @Autowired
    public UserController(UserServiceIml userServiceIml, ExportExcelIml exportExcelIml) {
        this.userServiceIml = userServiceIml;
        this.exportExcelIml = exportExcelIml;
    }

//    Get all user not use filter
    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllUsers() {
        List<UserResponseDTO> userDTOList = userServiceIml.findAllUser();
        return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDTOList), HttpStatus.OK);
    }


//    Create user
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createUser(@RequestBody UserEntity userEntity){
        try {
            UserLoginResponseDTO userLoginResponseDTO = userServiceIml.saveUser(userEntity);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("success", "User created successfully", userLoginResponseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );        }
    }

//    Auth login
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody UserEntity loginUser) {
        try {
            UserLoginResponseDTO userLoginResponseDTO = userServiceIml.loginUser(loginUser);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Login successful", userLoginResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Update user data
    @PutMapping("/update/{userId}")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable Long userId, @RequestBody UserEntity updatedUser) {
        try {
            UserResponseDTO userResponseDTO = userServiceIml.findUserById(userId, updatedUser);
             return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Update user successfully", userResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Delete user
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable Long userId) {
        try {
            UserResponseDTO userResponseDTO = userServiceIml.deleteUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Delete user successfully", userResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder

    ) {
        try {
            List<UserResponseDTO> userDTOList = userServiceIml.findAllUserFilter(id,name, phoneNumber, fromDate, toDate, page, size,sortBy, sortOrder);
            return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDTOList), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/exportToExcel")
    public ResponseEntity<String> exportToExcel(HttpServletResponse response,
                                                @RequestParam(required = false) Long id,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String phoneNumber,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortOrder
                                                ) {
        try {
            List<UserResponseDTO> users =  userServiceIml.findAllUserFilter(id,name, phoneNumber, fromDate, toDate, page, size,sortBy, sortOrder);
            exportExcelIml.exportToExcel(users, response);
            return ResponseEntity.ok("Excel export successful");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }


}
