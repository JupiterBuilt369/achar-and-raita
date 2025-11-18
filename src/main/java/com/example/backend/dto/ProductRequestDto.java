package com.example.backend.dto;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long categoryId;
    private Long regionId;
//    private String imageUrl;
}
