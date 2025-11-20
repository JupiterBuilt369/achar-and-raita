package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String ownerName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String phone;
    private String businessAddress;

    private String gstNumber;
    private String panNumber;
    private String bankDetails;

    private boolean verified; // Admin verifies sellers
}
