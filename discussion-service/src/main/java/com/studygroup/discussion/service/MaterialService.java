package com.studygroup.discussion.service;

import com.studygroup.discussion.aspect.RequireMembership;
import com.studygroup.discussion.dto.CreateMaterialRequest;
import com.studygroup.discussion.dto.MaterialDto;
import com.studygroup.discussion.exception.ForbiddenException;
import com.studygroup.discussion.exception.ResourceNotFoundException;
import com.studygroup.discussion.model.Material;
import com.studygroup.discussion.repository.MaterialRepository;
import com.studygroup.discussion.security.AuthenticatedUser;
import com.studygroup.discussion.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;

    @RequireMembership
    public MaterialDto uploadMaterial(Long groupId, CreateMaterialRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();

        Material material = Material.builder()
                .groupId(groupId)
                .uploaderId(currentUser.getUserId())
                .uploaderName(currentUser.getUsername())
                .title(request.getTitle())
                .description(request.getDescription())
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .fileSize(request.getFileSize())
                .build();

        Material saved = materialRepository.save(material);
        log.info("Material uploaded: id={}, groupId={}, uploaderId={}", saved.getId(), groupId, currentUser.getUserId());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    @RequireMembership
    public Page<MaterialDto> getMaterialsByGroup(Long groupId, Pageable pageable) {
        return materialRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public MaterialDto getMaterialById(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        return toDto(material);
    }

    public MaterialDto updateMaterial(Long materialId, CreateMaterialRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        if (!material.getUploaderId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Only the uploader or an admin can update this material");
        }

        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setFileUrl(request.getFileUrl());
        material.setFileName(request.getFileName());
        material.setFileType(request.getFileType());
        material.setFileSize(request.getFileSize());

        Material saved = materialRepository.save(material);
        log.info("Material updated: id={}", materialId);
        return toDto(saved);
    }

    public void deleteMaterial(Long materialId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        if (!material.getUploaderId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Only the uploader or an admin can delete this material");
        }

        materialRepository.delete(material);
        log.info("Material deleted: id={}", materialId);
    }

    private MaterialDto toDto(Material m) {
        return MaterialDto.builder()
                .id(m.getId())
                .groupId(m.getGroupId())
                .uploaderId(m.getUploaderId())
                .uploaderName(m.getUploaderName())
                .title(m.getTitle())
                .description(m.getDescription())
                .fileUrl(m.getFileUrl())
                .fileName(m.getFileName())
                .fileType(m.getFileType())
                .fileSize(m.getFileSize())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
