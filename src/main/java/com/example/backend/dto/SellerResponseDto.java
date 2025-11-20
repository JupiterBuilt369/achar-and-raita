package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerResponseDto {
    private Long id;
    private String shopName;
    private String ownerName;
    private String email;
    private String phone;
    private String businessAddress;
    private String gstNumber;
    private String panNumber;
    private boolean verified;
}