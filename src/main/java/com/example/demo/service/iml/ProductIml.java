package com.example.demo.service.iml;

import com.example.demo.model.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductIml implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductIml(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        productRepository.save(product);
        return product;
    }
}
