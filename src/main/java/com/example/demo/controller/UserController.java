package com.example.demo.controller;

import com.example.demo.common.ResponseObject;
import com.example.demo.model.dto.PasswordChangeDTO;
import com.example.demo.model.dto.UserDataResponseDTO;
import com.example.demo.model.dto.UserLoginResponseDTO;
import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.service.iml.ExportExcelIml;
import com.example.demo.service.iml.UserServiceIml;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    //   Get User
    @PostMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getUserByAdmin(@RequestBody UserEntity userEntity, @PathVariable String userId) {
        try {
            UserLoginResponseDTO userLoginResponseDTO = userServiceIml.findUserById(userEntity.getUserId(), userId);
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
    public ResponseEntity<ResponseObject> updateUser(@PathVariable String userId, @RequestBody UserEntity updatedUser) {
        try {
            UserLoginResponseDTO userLoginResponseDTO = userServiceIml.findUserByIdAndUpdate(userId, updatedUser);
             return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Update user successfully", userLoginResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Delete user
    @PostMapping("/delete/{userId}")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable String userId,  @RequestBody UserEntity requestUser) {
        try {
            UserResponseDTO userResponseDTO = userServiceIml.deleteUserById(userId, requestUser.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Delete user successfully", userResponseDTO)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

    //    Change password user
    @PostMapping("/change-password/{userId}")
    public ResponseEntity<ResponseObject> changePassword(@PathVariable String userId,  @RequestBody PasswordChangeDTO passwordChangeDTO) {
        try {
            Boolean checkChange = userServiceIml.changePassword(userId, passwordChangeDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Change password user successfully", checkChange)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Get all user using filter
    @PostMapping("/getUser")
    public ResponseEntity<ResponseObject> getUserByFilter(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobilePhone,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortOrder

    ) {
        try {
            Page<UserResponseDTO> userDTOPage = userServiceIml.findAllUserFilter(userId, name, mobilePhone, fromDate, toDate, page, size, sortBy, sortOrder);
            List<UserResponseDTO> userDTOList = userDTOPage.getContent();
            long totalPage = userDTOPage.getTotalPages();

            UserDataResponseDTO userDataResponseDTO = new UserDataResponseDTO(userDTOList, totalPage);
            return new ResponseEntity<>(new ResponseObject("success", "Users retrieved successfully", userDataResponseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/exportToExcel")
    public ResponseEntity<String> exportToExcel(HttpServletResponse response,
                                                @RequestParam(required = false) String userId,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String mobilePhone,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                                @RequestParam(defaultValue = "0",required = false) int page,
                                                @RequestParam(defaultValue = "10", required = false) int size,
                                                @RequestParam(required = false) String sortBy,
                                                @RequestParam(defaultValue = "asc", required = false) String sortOrder
                                                ) {
        try {
            List<UserResponseDTO> users =  userServiceIml.findAllUserFilterExport(userId,name, mobilePhone, fromDate, toDate, page, size,sortBy, sortOrder);
            exportExcelIml.exportToExcel(users, response);
            return ResponseEntity.ok("Excel export successful");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }


}
