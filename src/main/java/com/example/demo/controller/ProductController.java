package com.example.demo.controller;


import com.example.demo.common.ResponseObject;
import com.example.demo.model.dto.ProductResponseDTO;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductImages;
import com.example.demo.service.iml.ProductImagesIml;
import com.example.demo.service.iml.ProductIml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/product")
@Slf4j
public class ProductController {
    private final ProductIml productIml;
    @Autowired
    public ProductController(ProductIml productIml) {
        this.productIml = productIml;
    }
//    Get product by product id
    @GetMapping("/get/{productId}")
    public ResponseEntity<ResponseObject> getProduct(@PathVariable Long productId) {
        try{
        ProductResponseDTO productResponseDTOS = productIml.getProductByProductId(productId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Get products successful", productResponseDTOS)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );        }
    }

//    Get all products and filter by name with pagination
    @PostMapping("/all")
    public ResponseEntity<ResponseObject> getProducts(@RequestParam(required = false) String name,
                                                      @RequestParam(defaultValue = "0",required = false) int page,
                                                      @RequestParam(defaultValue = "10", required = false) int size) {
        try{
            Page<ProductResponseDTO> productResponseDTOS = productIml.findAllProductsFilter(name, page, size);
            List<ProductResponseDTO> productResponseDTOList = productResponseDTOS.stream().toList();
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Get products successful", productResponseDTOList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "Internal Server Error: " + e.getMessage(), null)
            );        }
    }


//    Create product
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createProduct(@ModelAttribute Product product, @RequestParam("file")MultipartFile[] files) {
        try {
            ProductResponseDTO saveProduct = productIml.saveProduct(product, files);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Create product successful", saveProduct)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Delete product
    @PostMapping("/delete/{productId}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long productId) {
        try {
            Product deleteProduct = productIml.deleteProductById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Delete product successful", deleteProduct)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

//    Update product
    @PutMapping("/update/{productId}")
    public ResponseEntity<ResponseObject> updateProduct(@ModelAttribute Product product,@RequestParam("file")MultipartFile[] files) {
        try {
            ProductResponseDTO saveProduct = productIml.updateProduct(product, files);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Edit product successful", saveProduct)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }

}
