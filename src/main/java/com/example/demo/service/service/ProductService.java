package com.example.demo.service.service;

import com.example.demo.model.dto.ProductResponseDTO;
import com.example.demo.model.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponseDTO saveProduct(Product product, MultipartFile[] files);
    Product deleteProductById(Long productId) throws Exception;
}
