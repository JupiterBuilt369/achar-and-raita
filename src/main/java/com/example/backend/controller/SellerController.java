package com.example.backend.controller;

import com.example.backend.dto.SellerRequestDto;
import com.example.backend.dto.SellerResponseDto;
import com.example.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping
    public SellerResponseDto register(@RequestBody SellerRequestDto request) {
        return sellerService.registerSeller(request);
    }

    @GetMapping("/{id}")
    public SellerResponseDto getSeller(@PathVariable Long id) {
        return sellerService.getSeller(id);
    }

    @GetMapping
    public List<SellerResponseDto> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @PutMapping("/{id}")
    public SellerResponseDto updateSeller(@PathVariable Long id,
                                          @RequestBody SellerRequestDto request) {
        return sellerService.updateSeller(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
    }
}
