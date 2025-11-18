package com.example.backend.service;

import com.example.backend.adapter.ProductAdapter;
import com.example.backend.dto.ProductRequestDto;
import com.example.backend.dto.ProductResponseDto;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.Region;
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
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequest, MultipartFile file) {

        // 1. Fetch existing product
        Product existing = getProductById(id);

        // 2. If a new file is given, upload it & replace old image
        if (file != null && !file.isEmpty()) {
            String newImageUrl = cloudinaryService.uploadFile(file);
            existing.setImageUrl(newImageUrl);
        }

        // 3. Update normal fields (name, description, etc.)
        existing.setName(productRequest.getName());
        existing.setDescription(productRequest.getDescription());
        existing.setPrice(productRequest.getPrice());
        existing.setStock(productRequest.getStock());

        // 4. Update relations (category, region)
        // Fetch Category & Region using IDs from RequestDTO
        Category category = categoryService.getCategoryById(productRequest.getCategoryId());
        Region region = regionService.getRegionById(productRequest.getRegionId());

        existing.setCategory(category);
        existing.setRegion(region);

        // 5. Save
        Product updatedProduct = productRepository.save(existing);

        // 6. Convert to response
        return ProductResponseDto.builder()
                .id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .stock(updatedProduct.getStock())
                .imageUrl(updatedProduct.getImageUrl())
                .categoryName(updatedProduct.getCategory().getName())
                .regionName(updatedProduct.getRegion().getName())
                .updatedAt(updatedProduct.getUpdatedAt())
                .createdAt(updatedProduct.getCreatedAt())
                .build();
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