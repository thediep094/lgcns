package com.example.demo.repository;

import com.example.demo.model.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {
    void deleteAllByProductId(Long productId);
    ProductImages findFirstByProductId(Long productId);
    List<ProductImages> findAllByProductId(Long productId);
}
