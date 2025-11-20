package com.example.backend.repository;

import com.example.backend.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByEmail(String email);
}
