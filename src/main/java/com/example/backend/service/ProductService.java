package com.example.backend.service;

import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto dto, List<MultipartFile> files);

    ProductResponseDto updateProduct(Long id, ProductRequestDto dto, List<MultipartFile> files);

    ProductResponseDto getProduct(Long id);

    void deleteProduct(Long id);

    // Pagination & Filtering
    Page<ProductResponseDto> getAllProducts(Pageable pageable);

    Page<ProductResponseDto> getProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponseDto> getProductsByRegion(Long regionId, Pageable pageable);

    Page<ProductResponseDto> searchProductsByName(String name, Pageable pageable);
}