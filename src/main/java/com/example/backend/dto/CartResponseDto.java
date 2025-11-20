package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private Long id;
    private Long userId;
    private Double totalCartPrice; // New field
    private List<CartItemResponseDto> items;
}