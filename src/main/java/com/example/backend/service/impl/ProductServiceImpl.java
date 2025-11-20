package com.example.backend.service.impl;

import com.example.backend.adapter.ProductAdapter;
import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.CloudinaryService;
import com.example.backend.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default to read-only for performance
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductAdapter productAdapter;
    private final CloudinaryService cloudinaryService;

    // Note: Seller/Category/Region repos are now used inside the Adapter to keep Service clean.

    @Override
    @Transactional // Write operation
    public ProductResponseDto createProduct(ProductRequestDto dto, List<MultipartFile> files) {
        logger.info("Attempting to create product: {}", dto.getName());

        try {
            // 1. Convert DTO to Entity (Adapter handles FK lookups)
            Product product = productAdapter.toEntity(dto);

            // 2. Upload Images (Multi-image support)
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String url = cloudinaryService.uploadFile(file);
                        product.getImageUrls().add(url);
                    }
                }
            }

            // 3. Save
            Product savedProduct = productRepository.save(product);

            logger.info("Product created successfully with ID: {}", savedProduct.getId());
            return productAdapter.toDto(savedProduct);

        } catch (DataAccessException e) {
            logger.error("Database error creating product: {}", e.getMessage());
            throw new RuntimeException("Service unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional // Write operation
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto, List<MultipartFile> files) {
        logger.info("Attempting to update product ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        try {
            // 1. Update Basic Fields via Adapter
            productAdapter.updateEntity(product, dto);

            // 2. Handle New Images (Append to existing list)
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String url = cloudinaryService.uploadFile(file);
                        product.getImageUrls().add(url);
                    }
                }
            }

            // 3. Save (Optimistic locking @Version is handled automatically by JPA here)
            Product savedProduct = productRepository.save(product);

            logger.info("Product updated successfully: {}", savedProduct.getId());
            return productAdapter.toDto(savedProduct);

        } catch (Exception e) {
            logger.error("Error updating product ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update product.");
        }
    }

    @Override
    public ProductResponseDto getProduct(Long id) {
        return productRepository.findById(id)
                .map(productAdapter::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with ID: " + id);
        }
        try {
            productRepository.deleteById(id);
            logger.info("Product ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting product: {}", e.getMessage());
            throw new RuntimeException("Unable to delete product due to data constraint.");
        }
    }

    // --- Pagination & Search Methods ---

    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productAdapter::toDto);
    }

    @Override
    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productAdapter::toDto);
    }

    @Override
    public Page<ProductResponseDto> getProductsByRegion(Long regionId, Pageable pageable) {
        return productRepository.findByRegionId(regionId, pageable)
                .map(productAdapter::toDto);
    }

    @Override
    public Page<ProductResponseDto> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(productAdapter::toDto);
    }
}