package com.example.demo.service.service;

import com.example.demo.model.entity.ProductImages;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImagesService {
    List<ProductImages> saveProductImages(MultipartFile[] files, Long productId);
    void deleteProductImagesByProductId(Long productId);
}
