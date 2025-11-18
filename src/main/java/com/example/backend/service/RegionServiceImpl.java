package com.example.backend.service.impl;

import com.example.backend.dto.RegionRequestDto;
import com.example.backend.dto.RegionResponseDto;
import com.example.backend.model.Region;
import com.example.backend.repository.RegionRepository;
import com.example.backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override
    public RegionResponseDto createRegion(RegionRequestDto request) {

        if (regionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Region already exists");
        }

        Region region = Region.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        regionRepository.save(region);

        return mapToDto(region);
    }

    @Override
    public RegionResponseDto getRegion(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found"));
        return mapToDto(region);
    }

    @Override
    public List<RegionResponseDto> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public RegionResponseDto updateRegion(Long id, RegionRequestDto request) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found"));

        region.setName(request.getName());
        region.setDescription(request.getDescription());

        regionRepository.save(region);

        return mapToDto(region);
    }

    @Override
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }

    private RegionResponseDto mapToDto(Region region) {
        RegionResponseDto dto = new RegionResponseDto();
        dto.setId(region.getId());
        dto.setName(region.getName());
        dto.setDescription(region.getDescription());
        return dto;
    }
}
