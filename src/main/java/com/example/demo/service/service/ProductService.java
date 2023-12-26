package com.example.demo.service.service;

import com.example.demo.model.dto.ProductResponseDTO;
import com.example.demo.model.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponseDTO saveProduct(Product product, MultipartFile[] files) throws Exception;
    Product deleteProductById(Long productId) throws Exception;
    ProductResponseDTO updateProduct(Product product, MultipartFile[] files) throws Exception;
}
