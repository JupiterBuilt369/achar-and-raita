package com.example.backend.controller;

import com.example.backend.dto.RegionRequestDto;
import com.example.backend.dto.RegionResponseDto;
import com.example.backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<RegionResponseDto> createRegion(@RequestBody RegionRequestDto request) {
        return ResponseEntity.ok(regionService.createRegion(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionResponseDto> getRegion(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getRegion(id));
    }

    @GetMapping
    public ResponseEntity<List<RegionResponseDto>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionResponseDto> updateRegion(
            @PathVariable Long id,
            @RequestBody RegionRequestDto request
    ) {
        return ResponseEntity.ok(regionService.updateRegion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.ok("Region deleted");
    }
}
