package com.example.backend.service;


import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto product , MultipartFile file);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    ProductResponseDto updateProduct(Long id, ProductRequestDto product , MultipartFile file);

    void deleteProduct(Long id);

    // Optional filtering/search
    List<Product> getProductsByCategory(Long categoryId);

    List<Product> getProductsByRegion(Long regionId);

    List<Product> searchProductsByName(String name);
}
