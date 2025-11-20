package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private int quantity;
    private Double price;
    private Double subTotal;
}