package com.example.demo.model.dto;

import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductImages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponseDTO {
    Product product;
    List<ProductImages> productImages;
}
