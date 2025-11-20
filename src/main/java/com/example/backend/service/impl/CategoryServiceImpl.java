package com.example.backend.service.impl;

import com.example.backend.dto.CategoryRequestDto;
import com.example.backend.dto.CategoryResponseDto;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        logger.info("Creating category: {}", request.getName());

        // 1. Check Duplicates (Case Insensitive)
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category '" + request.getName() + "' already exists");
        }

        // 2. Build Entity
        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
        return mapToDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        logger.info("Updating category ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));

        // Check collision only if name matches another existing category (ignoring case)
        // and it's NOT the current category's own name
        if (!category.getName().equalsIgnoreCase(request.getName()) &&
                categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Category name '" + request.getName() + "' is already taken");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
        logger.info("Category ID {} deleted successfully", id);
    }

    private CategoryResponseDto mapToDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}