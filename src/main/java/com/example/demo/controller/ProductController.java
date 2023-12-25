package com.example.demo.controller;


import com.example.demo.common.ResponseObject;
import com.example.demo.model.entity.Product;
import com.example.demo.service.iml.ProductIml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    Create product
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createProduct(@ModelAttribute Product product, @RequestParam("file")MultipartFile[] files) {
        try {
//            Product saveProduct = productIml.saveProduct(product);
//            List<ProductImages> saveProductImages = productImages.saveProductImages(files, saveProduct.getId());
            System.out.println(product);
            System.out.println(files);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success", "Login successful", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", e.getMessage(), null)
            );
        }
    }
}
