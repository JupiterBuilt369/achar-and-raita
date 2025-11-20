package com.example.backend.service;

import com.example.backend.dto.UserRequestDto;
import com.example.backend.dto.UserResponseDto;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimize for read operations by default
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    // REMOVED: PasswordEncoder dependency

    @Override
    @Transactional // Enable write transaction
    public UserResponseDto createUser(UserRequestDto request) {
        logger.info("Attempting to create user with email: {}", request.getEmail());

        // 1. Strict Input Validation
        validateUserRequest(request);

        // 2. Check for Duplicate Email
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Creation failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // 3. Build User (Storing PLAIN TEXT password as requested)
        User user = User.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword()) // WARNING: Plain text storage
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // Enable write transaction
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        logger.info("Attempting to update user ID: {}", id);

        // 1. Validate Input
        validateUserRequest(request);

        // 2. Fetch Existing User
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        // 3. Check Email Collision
        // logic: If email is changing, ensure the NEW email isn't taken by SOMEONE ELSE
        if (!existingUser.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email " + request.getEmail() + " is already in use by another account");
        }

        // 4. Update Fields
        existingUser.setFullName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhone(request.getPhone());

        // 5. Update Password (only if provided in request)
        if (StringUtils.hasText(request.getPassword())) {
            existingUser.setPassword(request.getPassword()); // Storing plain text
        }

        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", id);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Attempting to delete user ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User deleted successfully");
    }

    // --- Helper Methods ---

    private void validateUserRequest(UserRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (!StringUtils.hasText(request.getEmail()) || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        // Basic length check, even without hashing
        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters");
        }
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }
}