package com.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotNull(message = "Region ID is required")
    private Long regionId;
}