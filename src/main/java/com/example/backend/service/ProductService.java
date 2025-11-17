package com.example.backend.service;


import com.example.backend.model.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    // Optional filtering/search
    List<Product> getProductsByCategory(Long categoryId);

    List<Product> getProductsByRegion(Long regionId);

    List<Product> searchProductsByName(String name);
}
