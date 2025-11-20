package com.example.backend.controller;

import com.example.backend.dto.CartItemRequestDto;
import com.example.backend.dto.CartResponseDto;
import com.example.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart") // Versioning added
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponseDto> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDto requestDto // @Valid triggers DTO checks
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, requestDto));
    }

    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartResponseDto> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}