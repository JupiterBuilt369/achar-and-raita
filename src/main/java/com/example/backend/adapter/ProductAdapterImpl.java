package com.example.backend.adapter;

import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductAdapterImpl implements ProductAdapter {

    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final RegionRepository regionRepository;

    @Override
    public ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                .sellerName(product.getSeller() != null ? product.getSeller().getOwnerName() : "N/A")
                .regionName(product.getRegion() != null ? product.getRegion().getName() : "N/A")
                .imageUrls(product.getImageUrls()) // Return the list
                .build();
    }

    @Override
    public Product toEntity(ProductRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with ID: " + dto.getSellerId()));

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException("Region not found with ID: " + dto.getRegionId()));

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .seller(seller)
                .region(region)
                // Image URLs are handled in the service
                .build();
    }

    @Override
    public void updateEntity(Product product, ProductRequestDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null && dto.getPrice() > 0) product.setPrice(dto.getPrice());
        if (dto.getStock() >= 0) product.setStock(dto.getStock());

        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found")));
        }
        if (dto.getSellerId() != null) {
            product.setSeller(sellerRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller not found")));
        }
        if (dto.getRegionId() != null) {
            product.setRegion(regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new EntityNotFoundException("Region not found")));
        }
    }
}