package com.example.demo.model.dto;

import com.example.demo.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private Long memberId;
    private String userId;
    private String name;
    private String mobilePhone;
    private String email;
    private Role role;
    private Date date;

}
