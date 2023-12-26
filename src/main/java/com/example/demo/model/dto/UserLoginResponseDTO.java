package com.example.demo.model.dto;

import com.example.demo.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class UserLoginResponseDTO extends UserResponseDTO{
    private String avatar;

    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(Long memberId, String userId, String name, String mobilePhone, String email, Role role, Date date, String avatar) {
        super(memberId, userId, name, mobilePhone, email, role, date);
        this.avatar = avatar;
    }
}
