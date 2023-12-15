package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDataResponseDTO {
    List<UserResponseDTO> users;
    Long totalPage;

    public UserDataResponseDTO(List<UserResponseDTO> users, Long totalPage) {
        this.users = users;
        this.totalPage = totalPage;
    }

    public UserDataResponseDTO() {
    }
}
