package com.example.backend.service;

import com.example.backend.adapter.ProductAdapter;
import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductAdapter productAdapter;
    private final CloudinaryService  cloudinaryService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductAdapter productAdapter,
                              CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.productAdapter = productAdapter;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto product , MultipartFile file) {


        String imageUrl = cloudinaryService.uploadFile(file);
        Product entityProduct = productAdapter.productRequestDtoToProductEntity(product);
        entityProduct.setImageUrl(imageUrl);

        Product createdProduct = productRepository.save(entityProduct);

        // 2. Build the response DTO
        return ProductResponseDto.builder()
                .id(createdProduct.getId())
                .name(createdProduct.getName())
                .description(createdProduct.getDescription())
                .price(createdProduct.getPrice())
                .stock(createdProduct.getStock())
                .imageUrl(createdProduct.getImageUrl())
                .categoryName(createdProduct.getCategory().getName())
                .regionName(createdProduct.getRegion().getName())
                .createdAt(createdProduct.getCreatedAt())
                .updatedAt(createdProduct.getUpdatedAt())
                .build();


    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public Product updateProduct(Long id, Product updated) {
        Product existing = getProductById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setImageUrl(updated.getImageUrl());
        existing.setStock(updated.getStock());
        existing.setCategory(updated.getCategory());
        existing.setRegion(updated.getRegion());

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Optional: filtering
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
}