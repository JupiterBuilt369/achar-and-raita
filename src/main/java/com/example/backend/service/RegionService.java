package com.example.backend.service;

import com.example.backend.dto.RegionRequestDto;
import com.example.backend.dto.RegionResponseDto;

import java.util.List;

public interface RegionService {

    RegionResponseDto createRegion(RegionRequestDto request);

    RegionResponseDto getRegion(Long id);

    List<RegionResponseDto> getAllRegions();

    RegionResponseDto updateRegion(Long id, RegionRequestDto request);

    void deleteRegion(Long id);
}
