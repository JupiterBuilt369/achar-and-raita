package com.example.backend.service;

import com.example.backend.dto.SellerRequestDto;
import com.example.backend.dto.SellerResponseDto;
import java.util.List;

public interface SellerService {
    SellerResponseDto registerSeller(SellerRequestDto request);
    SellerResponseDto getSeller(Long id);
    List<SellerResponseDto> getAllSellers();
    SellerResponseDto updateSeller(Long id, SellerRequestDto request);
    void deleteSeller(Long id);
}