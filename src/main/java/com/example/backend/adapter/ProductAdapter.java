package com.example.backend.adapter;

import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Product;

public interface ProductAdapter {
    ProductResponseDto toDto(Product product);
    Product toEntity(ProductRequestDto dto);
    void updateEntity(Product product, ProductRequestDto dto);
}