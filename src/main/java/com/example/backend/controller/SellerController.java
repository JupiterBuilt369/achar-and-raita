package com.example.backend.controller;

import com.example.backend.dto.SellerRequestDto;
import com.example.backend.dto.SellerResponseDto;
import com.example.backend.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping
    public ResponseEntity<SellerResponseDto> register(@Valid @RequestBody SellerRequestDto request) {
        SellerResponseDto response = sellerService.registerSeller(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDto> getSeller(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.getSeller(id));
    }

    @GetMapping
    public ResponseEntity<List<SellerResponseDto>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerResponseDto> updateSeller(
            @PathVariable Long id,
            @Valid @RequestBody SellerRequestDto request) {
        return ResponseEntity.ok(sellerService.updateSeller(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.ok("Seller deleted successfully");
    }
}