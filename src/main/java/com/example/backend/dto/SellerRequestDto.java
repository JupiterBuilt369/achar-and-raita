package com.example.backend.dto;

import lombok.Data;

@Data
public class SellerRequestDto {

    private String shopName;
    private String ownerName;
    private String email;
    private String password;
    private String phone;
    private String businessAddress;
    private String gstNumber;
    private String panNumber;
    private String bankDetails;
}
