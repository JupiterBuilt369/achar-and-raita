package com.example.backend.service.impl;

import com.example.backend.dto.SellerRequestDto;
import com.example.backend.dto.SellerResponseDto;
import com.example.backend.model.Seller;
import com.example.backend.repository.SellerRepository;
import com.example.backend.service.SellerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerServiceImpl implements SellerService {

    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);
    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public SellerResponseDto registerSeller(SellerRequestDto request) {
        logger.info("Registering new seller: {}", request.getEmail());

        // 1. Check Duplicates
        if (sellerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Seller email already registered");
        }

        // 2. Build Seller
        Seller seller = Seller.builder()
                .shopName(request.getShopName().trim())
                .ownerName(request.getOwnerName().trim())
                .email(request.getEmail().trim())
                .password(request.getPassword()) // Plain text as requested (Update to BCrypt later)
                .phone(request.getPhone())
                .businessAddress(request.getBusinessAddress())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .bankDetails(request.getBankDetails())
                .verified(false)
                .build();

        Seller saved = sellerRepository.save(seller);
        logger.info("Seller registered successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    @Override
    public SellerResponseDto getSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with ID: " + id));
        return mapToDto(seller);
    }

    @Override
    public List<SellerResponseDto> getAllSellers() {
        return sellerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SellerResponseDto updateSeller(Long id, SellerRequestDto request) {
        logger.info("Updating seller profile ID: {}", id);

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with ID: " + id));

        // Check email collision ONLY if email is changing
        if (!seller.getEmail().equalsIgnoreCase(request.getEmail()) &&
                sellerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already used by another seller");
        }

        seller.setShopName(request.getShopName());
        seller.setOwnerName(request.getOwnerName());
        seller.setEmail(request.getEmail()); // Allow email update
        seller.setPhone(request.getPhone());
        seller.setBusinessAddress(request.getBusinessAddress());
        seller.setPanNumber(request.getPanNumber());
        seller.setGstNumber(request.getGstNumber());
        seller.setBankDetails(request.getBankDetails());

        // Update password only if provided and not empty
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            seller.setPassword(request.getPassword());
        }

        Seller updated = sellerRepository.save(seller);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteSeller(Long id) {
        if (!sellerRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Seller not found with ID: " + id);
        }
        sellerRepository.deleteById(id);
        logger.info("Seller ID {} deleted successfully", id);
    }

    private SellerResponseDto mapToDto(Seller seller) {
        return SellerResponseDto.builder()
                .id(seller.getId())
                .shopName(seller.getShopName())
                .ownerName(seller.getOwnerName())
                .email(seller.getEmail())
                .phone(seller.getPhone())
                .businessAddress(seller.getBusinessAddress())
                .gstNumber(seller.getGstNumber())
                .panNumber(seller.getPanNumber())
                .verified(seller.isVerified())
                .build();
    }
}