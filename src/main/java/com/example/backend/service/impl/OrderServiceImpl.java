package com.example.backend.service.impl;

import com.example.backend.dto.*;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.example.backend.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Ensures Order Save + Stock Update + Cart Clear happen together
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository; // Needed for stock update

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto request) {

        // 1. Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. Validate Cart
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order. Cart is empty.");
        }

        // 3. Create Order Entity
        Order order = Order.builder()
                .user(user)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(0.0) // Will calculate below
                .build();

        double calculatedTotal = 0.0;

        // 4. Process Items & Deduct Stock
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // A. Stock Check
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            // B. Deduct Stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // C. Create Order Item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .subTotal(cartItem.getTotalPrice())
                    .build();

            order.getItems().add(orderItem);
            calculatedTotal += cartItem.getTotalPrice();
        }

        order.setTotalAmount(calculatedTotal);

        // 5. Save Order
        Order savedOrder = orderRepository.save(order);

        // 6. Clear Cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponseDto> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponseDto mapToResponse(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderedAt(order.getCreatedAt()) // From BaseEntity
                .items(order.getItems().stream().map(item -> OrderItemResponseDto.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subTotal(item.getSubTotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}