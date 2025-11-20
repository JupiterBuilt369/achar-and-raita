package com.example.backend.service;

import com.example.backend.dto.OrderRequestDto;
import com.example.backend.dto.OrderResponseDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto request);
    OrderResponseDto getOrder(Long orderId);
    List<OrderResponseDto> getUserOrders(Long userId);
}