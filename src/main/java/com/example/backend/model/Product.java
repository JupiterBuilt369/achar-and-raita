package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    private String imageUrl;

    @Column(nullable = false)
    private Integer stock;

    // Many products belong to one category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Many products belong to one region
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
    }