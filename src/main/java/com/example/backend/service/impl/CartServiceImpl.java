package com.example.backend.service.impl;

import com.example.backend.dto.CartItemResponseDto;
import com.example.backend.dto.CartItemRequestDto;
import com.example.backend.dto.CartResponseDto;
import com.example.backend.model.Cart;
import com.example.backend.model.CartItem;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponseDto getUserCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToDto(cart);
    }

    @Override
    public CartResponseDto addItemToCart(Long userId, CartItemRequestDto request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + request.getProductId()));

        // 1. Stock Check
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        // 2. Check if item exists in cart (Merge logic)
        Optional<CartItem> existingItemOpt = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            // Update existing item
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Re-check stock for the TOTAL new quantity
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock to add more of: " + product.getName());
            }

            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(newQuantity * product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            // Create new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(product.getPrice());
            newItem.setTotalPrice(request.getQuantity() * product.getPrice());
            cartItemRepository.save(newItem);
        }

        return getUserCart(userId); // Return updated cart state
    }

    @Override
    public CartResponseDto removeItem(Long userId, Long cartItemId) {
        // Validate cart ownership before deleting (Security best practice)
        Cart cart = getOrCreateCart(userId);

        CartItem itemToDelete = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found"));

        if (!itemToDelete.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("You are not authorized to remove this item");
        }

        cartItemRepository.delete(itemToDelete);
        return getUserCart(userId);
    }

    @Override
    public CartResponseDto clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        // Use orphanRemoval logic or manual delete
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        return convertToDto(cartRepository.save(cart));
    }

    // --- Helper Methods ---

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponseDto convertToDto(Cart cart) {
        CartResponseDto response = new CartResponseDto();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());

        // Calculate Cart Total safely
        double cartTotal = 0.0;

        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            List<CartItemResponseDto> itemsDto = cart.getCartItems().stream().map(item -> {
                CartItemResponseDto dto = new CartItemResponseDto();
                dto.setId(item.getId());
                dto.setProductId(item.getProduct().getId());
                dto.setProductName(item.getProduct().getName());
                dto.setQuantity(item.getQuantity());
                dto.setPrice(item.getPrice());
                dto.setTotalPrice(item.getTotalPrice());
                return dto;
            }).collect(Collectors.toList());

            response.setItems(itemsDto);

            // Sum up totals
            cartTotal = itemsDto.stream().mapToDouble(CartItemResponseDto::getTotalPrice).sum();
        }

        response.setTotalCartPrice(cartTotal);
        return response;
    }
}