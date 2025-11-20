package com.example.backend.service;

import com.example.backend.dto.SellerRequestDto;
import com.example.backend.dto.SellerResponseDto;
import com.example.backend.model.Seller;
import com.example.backend.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;

    @Override
    public SellerResponseDto registerSeller(SellerRequestDto request) {

        if (sellerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Seller already exists with email: " + request.getEmail());
        }

        Seller seller = Seller.builder()
                .shopName(request.getShopName())
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .password(request.getPassword()) // Encrypt later
                .phone(request.getPhone())
                .businessAddress(request.getBusinessAddress())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .bankDetails(request.getBankDetails())
                .verified(false)
                .build();

        Seller saved = sellerRepository.save(seller);

        return SellerResponseDto.builder()
                .id(saved.getId())
                .shopName(saved.getShopName())
                .ownerName(saved.getOwnerName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .verified(saved.isVerified())
                .build();
    }

    @Override
    public SellerResponseDto getSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return SellerResponseDto.builder()
                .id(seller.getId())
                .shopName(seller.getShopName())
                .ownerName(seller.getOwnerName())
                .email(seller.getEmail())
                .phone(seller.getPhone())
                .verified(seller.isVerified())
                .build();
    }

    @Override
    public List<SellerResponseDto> getAllSellers() {
        return sellerRepository.findAll()
                .stream().map(seller ->
                        SellerResponseDto.builder()
                                .id(seller.getId())
                                .shopName(seller.getShopName())
                                .ownerName(seller.getOwnerName())
                                .email(seller.getEmail())
                                .phone(seller.getPhone())
                                .verified(seller.isVerified())
                                .build()
                ).toList();
    }

    @Override
    public SellerResponseDto updateSeller(Long id, SellerRequestDto request) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        seller.setShopName(request.getShopName());
        seller.setOwnerName(request.getOwnerName());
        seller.setPhone(request.getPhone());
        seller.setBusinessAddress(request.getBusinessAddress());
        seller.setPanNumber(request.getPanNumber());
        seller.setGstNumber(request.getGstNumber());
        seller.setBankDetails(request.getBankDetails());

        Seller updated = sellerRepository.save(seller);

        return SellerResponseDto.builder()
                .id(updated.getId())
                .shopName(updated.getShopName())
                .ownerName(updated.getOwnerName())
                .email(updated.getEmail())
                .phone(updated.getPhone())
                .verified(updated.isVerified())
                .build();
    }

    @Override
    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }
}
