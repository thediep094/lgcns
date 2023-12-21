package com.example.demo.repository;

import com.example.demo.model.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    List<Avatar> findAllByMemberId(Long userId);
    Avatar findFirstByMemberId(Long userId);
}
