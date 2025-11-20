package com.example.backend.repository;

import com.example.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Return Pages instead of Lists for performance
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByRegionId(Long regionId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}