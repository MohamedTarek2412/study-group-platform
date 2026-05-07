package com.studygroup.discussion.controller;

import com.studygroup.discussion.dto.*;
import com.studygroup.discussion.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
@Slf4j
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/groups/{groupId}/materials")
    public ResponseEntity<ApiResponse<MaterialDto>> uploadMaterial(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateMaterialRequest request) {
        log.info("Uploading material to group {}", groupId);
        request.setGroupId(groupId);
        MaterialDto material = materialService.uploadMaterial(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MaterialDto>builder()
                        .success(true)
                        .message("Material uploaded successfully")
                        .data(material)
                        .build());
    }

    @GetMapping("/groups/{groupId}/materials")
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> getMaterialsByGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching materials for group {}", groupId);
        Pageable pageable = PageRequest.of(page, size);
        Page<MaterialDto> materials = materialService.getMaterialsByGroup(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<MaterialDto>>builder()
                .success(true)
                .message("Materials fetched successfully")
                .data(materials)
                .build());
    }

    @GetMapping("/materials/{materialId}")
    public ResponseEntity<ApiResponse<MaterialDto>> getMaterialById(@PathVariable Long materialId) {
        log.info("Fetching material {}", materialId);
        MaterialDto material = materialService.getMaterialById(materialId);
        return ResponseEntity.ok(ApiResponse.<MaterialDto>builder()
                .success(true)
                .message("Material fetched successfully")
                .data(material)
                .build());
    }

    @PutMapping("/materials/{materialId}")
    public ResponseEntity<ApiResponse<MaterialDto>> updateMaterial(
            @PathVariable Long materialId,
            @Valid @RequestBody CreateMaterialRequest request) {
        log.info("Updating material {}", materialId);
        MaterialDto material = materialService.updateMaterial(materialId, request);
        return ResponseEntity.ok(ApiResponse.<MaterialDto>builder()
                .success(true)
                .message("Material updated successfully")
                .data(material)
                .build());
    }

    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long materialId) {
        log.info("Deleting material {}", materialId);
        materialService.deleteMaterial(materialId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Material deleted successfully")
                .build());
    }
}
