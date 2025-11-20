package com.example.backend.service;

import com.example.backend.adapter.ProductAdapter;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductAdapter productAdapter;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final SellerRepository sellerRepository;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request, MultipartFile file) {
        logger.info("Attempting to create product: {}", request.getName());

        // 1. Validate Input Data
        validateProductRequest(request);

        // 2. Validate File existence for creation
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Product image is required for creation.");
        }

        try {
            // 3. Upload Image (External Service Call)
            String imageUrl = cloudinaryService.uploadFile(file);

            // 4. Map and Save
            Product entityProduct = productAdapter.productRequestDtoToProductEntity(request);
            entityProduct.setImageUrl(imageUrl);

            Product createdProduct = productRepository.save(entityProduct);

            logger.info("Product created successfully with ID: {}", createdProduct.getId());
            return mapToResponse(createdProduct);

        } catch (DataAccessException e) {
            logger.error("Database error while creating product: {}", e.getMessage());
            throw new RuntimeException("Database service is currently unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error creating product: {}", e.getMessage());
            throw new RuntimeException("Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request, MultipartFile file) {
        logger.info("Attempting to update product with ID: {}", id);

        // 1. Validate Input
        if (id == null) throw new IllegalArgumentException("Product ID cannot be null");
        validateProductRequest(request);

        try {
            // 2. Fetch Existing Product
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

            // 3. Validate and Fetch Relationships
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));

            Region region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new EntityNotFoundException("Region not found with id: " + request.getRegionId()));

            Seller seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + request.getSellerId()));

            // 4. Update Basic Fields
            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            existingProduct.setStock(request.getStock());

            // 5. Update Relationships
            existingProduct.setCategory(category);
            existingProduct.setRegion(region);
            existingProduct.setSeller(seller);

            // 6. Handle Image Upload safely
            if (file != null && !file.isEmpty()) {
                logger.info("New file detected. Uploading to Cloudinary...");
                String newImageUrl = uploadImageSafely(file);
                existingProduct.setImageUrl(newImageUrl);
            }

            // 7. Save
            Product savedProduct = productRepository.save(existingProduct);

            logger.info("Product updated successfully: {}", savedProduct.getId());
            return mapToResponse(savedProduct);

        } catch (EntityNotFoundException e) {
            logger.warn("Update failed: {}", e.getMessage());
            throw e; // Re-throw to let Controller handle 404
        } catch (Exception e) {
            logger.error("Error updating product ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update product due to system error.");
        }
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Attempting to delete product with ID: {}", id);
        try {
            if (!productRepository.existsById(id)) {
                throw new EntityNotFoundException("Cannot delete. Product not found with id: " + id);
            }
            productRepository.deleteById(id);
            logger.info("Product deleted successfully.");
        } catch (DataAccessException e) {
            logger.error("Database error deleting product: {}", e.getMessage());
            throw new RuntimeException("Unable to delete product due to database error.");
        }
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to fetch all products", e);
            throw new RuntimeException("Unable to retrieve products.");
        }
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getProductsByRegion(Long regionId) {
        return productRepository.findByRegionId(regionId);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // --- Helper Methods for Clean Code & Fault Tolerance ---

    /**
     * Validates business rules for the product.
     * Prevents bad data from ever reaching logic/DB.
     */
    private void validateProductRequest(ProductRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new IllegalArgumentException("Price must be a non-negative value");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (request.getCategoryId() == null || request.getRegionId() == null || request.getSellerId() == null) {
            throw new IllegalArgumentException("Category, Region, and Seller IDs are required");
        }
    }

    /**
     * Wrapper for Cloudinary upload to separate external service failure logic.
     */
    private String uploadImageSafely(MultipartFile file) {
        try {
            return cloudinaryService.uploadFile(file);
        } catch (Exception e) {
            logger.error("Cloudinary upload failed: {}", e.getMessage());
            throw new RuntimeException("Image upload service failed. Please try again.");
        }
    }

    private ProductResponseDto mapToResponse(Product p) {
        return ProductResponseDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .categoryName(p.getCategory().getName())
                .regionName(p.getRegion().getName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}