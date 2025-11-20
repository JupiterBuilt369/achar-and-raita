package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;      // Unit price
    private double totalPrice; // quantity * unit price
}