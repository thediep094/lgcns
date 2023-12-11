package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class UserEntity {

    @Id
    private Long id;
    private String password;
    private String name;
    private String mobile_phone;
    private String email;

}
