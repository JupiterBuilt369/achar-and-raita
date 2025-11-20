package com.example.backend.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private int stock;
    private String categoryName;
    private String sellerName;
    private String regionName;
    private List<String> imageUrls; // Returns list of images
}