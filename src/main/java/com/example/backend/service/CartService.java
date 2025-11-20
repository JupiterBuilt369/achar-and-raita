package com.example.backend.service;

import com.example.backend.dto.CartItemRequestDto;
import com.example.backend.dto.CartResponseDto;

public interface CartService {
    CartResponseDto getUserCart(Long userId);
    CartResponseDto addItemToCart(Long userId, CartItemRequestDto requestDto);
    CartResponseDto removeItem(Long userId, Long cartItemId);
    CartResponseDto clearCart(Long userId);
}