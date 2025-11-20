package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime orderedAt;
    private List<OrderItemResponseDto> items;
}