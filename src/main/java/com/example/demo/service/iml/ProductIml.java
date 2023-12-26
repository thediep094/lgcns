package com.example.demo.service.iml;

import com.example.demo.model.dto.ProductResponseDTO;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductImages;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.service.ProductService;
import com.example.demo.specification.ProductSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductIml implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImagesIml productImagesIml;    @Autowired
    public ProductIml(ProductRepository productRepository, ProductImagesIml productImagesIml) {
        this.productRepository = productRepository;
        this.productImagesIml = productImagesIml;
    }

    public ProductResponseDTO saveProduct(Product product, MultipartFile[] files) {
        Product saveProduct = productRepository.save(product);
        List<ProductImages> saveProductImages = productImagesIml.saveProductImages(files, saveProduct.getId());
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(saveProduct, saveProductImages);

        return productResponseDTO;
    }

    public Product deleteProductById(Long productId) throws Exception{
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()) {
            Product exitstingProduct = product.get();
            productRepository.deleteById(productId);
            productImagesIml.deleteProductImagesByProductId(productId);
            return exitstingProduct;
        } else {
            throw new Exception("Product not found!!");
        }
    }

    public Page<ProductResponseDTO> findAllProductsFilter(String name,
                                                          int page,
                                                          int size) {
        Specification<Product> specification = Specification.where(null);
        if(name != null) {
            specification = specification.and(ProductSpecifications.nameLike(name));
        }
        Pageable pageable;
        pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(specification, pageable);
        return products.map(product -> {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setProduct(product);
            List<ProductImages> productImages = productImagesIml.findAllProductImages(product.getId());
             productResponseDTO.setProductImages(productImages);
            return productResponseDTO;
        });
    }

    public ProductResponseDTO updateProduct(Product product, MultipartFile[] files) throws Exception{
        Optional<Product> findProduct = productRepository.findById(product.getId());
        if(findProduct.isPresent()) {
            Product newProduct = productRepository.save(product);
            List<ProductImages> productImages = productImagesIml.updateProductImagesByProductId(product.getId(), files);
            ProductResponseDTO productResponseDTO = new ProductResponseDTO(newProduct, productImages);
            return productResponseDTO;
        } else {
            throw new Exception("Product not exits!!");
        }
    }
}
