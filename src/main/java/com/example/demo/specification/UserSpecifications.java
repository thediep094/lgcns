package com.example.demo.specification;

import com.example.demo.model.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class UserSpecifications {

    public static Specification<UserEntity> idEqual(Long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<UserEntity> idPartialMatch(String partialId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("id").as(String.class), "%" + partialId + "%");
    }
    public static Specification<UserEntity> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<UserEntity> mobilePhoneLike(String mobilePhone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("mobilePhone"), "%" + mobilePhone + "%");
    }

    public static Specification<UserEntity> dateBetween(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("date"), fromDate, toDate);
    }
}
