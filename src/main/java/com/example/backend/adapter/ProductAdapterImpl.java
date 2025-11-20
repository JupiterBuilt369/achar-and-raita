package com.example.backend.adapter;

import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.Region;
import com.example.backend.model.Seller;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.RegionRepository;
import com.example.backend.repository.SellerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductAdapterImpl implements ProductAdapter{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final SellerRepository sellerRepository;

    public ProductAdapterImpl(ProductRepository productRepository, CategoryRepository categoryRepository, SellerRepository sellerRepository, RegionRepository regionRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.regionRepository = regionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Product productRequestDtoToProductEntity(ProductRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() ->
                        new RuntimeException("Region not found with id: " + dto.getRegionId()));
        Seller seller = sellerRepository.findById(dto.getSellerId())
                .orElseThrow(() ->
                        new RuntimeException("Region not found with id: " + dto.getSellerId()));

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
//                .imageUrl(dto.getImageUrl())
                .stock(dto.getStock())
                .category(category)
                .region(region)
                .seller(seller)
                .build();
    }

    @Override
    public ProductResponseDto productEntityToProductResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .categoryName(product.getCategory().getName())
                .regionName(product.getRegion().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
