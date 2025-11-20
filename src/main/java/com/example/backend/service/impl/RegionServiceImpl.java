package com.example.backend.service.impl;

import com.example.backend.dto.RegionRequestDto;
import com.example.backend.dto.RegionResponseDto;
import com.example.backend.model.Region;
import com.example.backend.repository.RegionRepository;
import com.example.backend.service.RegionService;
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
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);
    private final RegionRepository regionRepository;

    @Override
    @Transactional
    public RegionResponseDto createRegion(RegionRequestDto request) {
        logger.info("Attempting to create region: {}", request.getName());

        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Region name is required");
        }

        if (regionRepository.existsByName(request.getName())) {
            logger.warn("Region creation failed. Name '{}' already exists.", request.getName());
            throw new IllegalArgumentException("Region '" + request.getName() + "' already exists");
        }

        Region region = Region.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();

        Region saved = regionRepository.save(region);
        logger.info("Region created successfully with ID: {}", saved.getId());

        return mapToDto(saved);
    }

    @Override
    public RegionResponseDto getRegion(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found with ID: " + id));
        return mapToDto(region);
    }

    @Override
    public List<RegionResponseDto> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RegionResponseDto updateRegion(Long id, RegionRequestDto request) {
        logger.info("Attempting to update region ID: {}", id);

        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Region name cannot be empty");
        }

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found with ID: " + id));

        if (!region.getName().equalsIgnoreCase(request.getName()) &&
                regionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Region name '" + request.getName() + "' is already taken");
        }

        region.setName(request.getName().trim());
        region.setDescription(request.getDescription());

        Region updated = regionRepository.save(region);
        logger.info("Region updated successfully: {}", id);

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteRegion(Long id) {
        logger.info("Attempting to delete region ID: {}", id);
        if (!regionRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Region not found with ID: " + id);
        }
        regionRepository.deleteById(id);
        logger.info("Region deleted successfully");
    }

    private RegionResponseDto mapToDto(Region region) {
        RegionResponseDto dto = new RegionResponseDto();
        dto.setId(region.getId());
        dto.setName(region.getName());
        dto.setDescription(region.getDescription());
        return dto;
    }
}