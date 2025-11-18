package com.example.backend.adapter;

import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Product;

public interface ProductAdapter {

    Product productRequestDtoToProductEntity(ProductRequestDto productRequestDto);
    ProductResponseDto productEntityToProductResponseDto(Product product);
}
