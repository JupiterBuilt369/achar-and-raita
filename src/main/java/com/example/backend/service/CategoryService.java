package com.example.backend.service;

import com.example.backend.dto.CategoryRequestDto;
import com.example.backend.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto request);
    CategoryResponseDto getCategory(Long id);
    List<CategoryResponseDto> getAllCategories();
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto request);
    void deleteCategory(Long id);
}