package com.example.demo.service.iml;

import com.example.demo.model.entity.Avatar;
import com.example.demo.model.entity.ProductImages;
import com.example.demo.repository.ProductImagesRepository;
import com.example.demo.service.service.ProductImagesService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductImagesIml implements ProductImagesService {
    private final ProductImagesRepository productImagesRepository;

    @Autowired
    public ProductImagesIml(ProductImagesRepository productImagesRepository) {
        this.productImagesRepository = productImagesRepository;
    }


    public List<ProductImages> findAllProductImages(Long productId) {
        List<ProductImages> productImages = productImagesRepository.findAllByProductId(productId);
        return productImages;
    }

    public List<ProductImages> saveProductImages(MultipartFile[] files, Long productId) {
        String imageUploadDirectory = "C:\\Users\\63200202\\Downloads\\Images";
        List<String> imageUrls = new ArrayList<>();
        List<ProductImages> productImagesList = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            try {
                Path directoryPath = Paths.get(imageUploadDirectory);
                String filePath = directoryPath.resolve(filename).toAbsolutePath().toString();

                // Save the file to the specified directory
                file.transferTo(new File(filePath));

                imageUrls.add(filename);
                log.info("Upload to upload image: {}", filename);
            } catch (IOException e) {
                // Handle the exception appropriately
                log.error("Failed to upload image: {}", e.getMessage());
            }
        }

        for (String imageUrl : imageUrls) {
            ProductImages productImages = new ProductImages();
            log.debug("productId: {}", productId);
            log.debug("imageUrl: {}", imageUrl);
            productImages.setProductId(productId);
            productImages.setUrl(imageUrl);
            productImagesRepository.save(productImages);
            productImagesList.add(productImages);
        }
        return productImagesList;
    }

    @Transactional
    public void deleteProductImagesByProductId(Long productId) {
        ProductImages productImages = productImagesRepository.findFirstByProductId(productId);
        if(productImages != null) {
            productImagesRepository.deleteAllByProductId(productId);
        }

    }

    @Transactional
    public List<ProductImages> updateProductImagesByProductId(Long productId, MultipartFile[] files) {
        this.deleteProductImagesByProductId(productId);
        List<ProductImages> productImages = this.saveProductImages(files, productId);
        return productImages;
    }
}
