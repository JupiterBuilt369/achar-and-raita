package com.example.backend.service;

import com.example.backend.dto.UserRequestDto;
import com.example.backend.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto request);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserRequestDto request);
    void deleteUser(Long id);
}