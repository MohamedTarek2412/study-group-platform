package com.studygroup.group.controller;

import com.studygroup.group.dto.ApiResponse;
import com.studygroup.group.dto.CreateGroupRequest;
import com.studygroup.group.dto.GroupDto;
import com.studygroup.group.service.GroupService;
import com.studygroup.group.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final JwtUtil jwtUtil;

    // Public endpoints

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GroupDto>>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all approved groups - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupDto> groups = groupService.getApprovedGroups(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<GroupDto>>builder()
                .success(true)
                .message("Groups fetched successfully")
                .data(groups)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDto>> getGroupById(@PathVariable Long id) {
        log.info("Fetching group with id: {}", id);
        GroupDto group = groupService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group fetched successfully")
                .data(group)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<GroupDto>>> searchGroups(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching groups with query: {}", q);
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupDto> groups = groupService.searchGroups(q, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<GroupDto>>builder()
                .success(true)
                .message("Search completed successfully")
                .data(groups)
                .build());
    }

    // Creator endpoints

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDto>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        String creatorName = extractUsernameFromToken(authHeader);
        log.info("Creating group for creator: {} ({})", creatorId, creatorName);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<GroupDto>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        GroupDto group = groupService.createGroup(creatorId, creatorName, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<GroupDto>builder()
                        .success(true)
                        .message("Group created successfully")
                        .data(group)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDto>> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody CreateGroupRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        log.info("Updating group {} for creator: {}", id, creatorId);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<GroupDto>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        GroupDto group = groupService.updateGroup(id, creatorId, request);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group updated successfully")
                .data(group)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGroup(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        log.info("Deleting group {} for creator: {}", id, creatorId);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        groupService.deleteGroup(id, creatorId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Group deleted successfully")
                .build());
    }

    // Admin endpoints

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<GroupDto>> approveGroup(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String role = extractRoleFromToken(authHeader);
        log.info("Approving group: {} (Admin role required)", id);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<GroupDto>builder()
                            .success(false)
                            .message("Admin role required")
                            .error("FORBIDDEN")
                            .build());
        }

        GroupDto group = groupService.approveGroup(id);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group approved successfully")
                .data(group)
                .build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<GroupDto>> rejectGroup(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String role = extractRoleFromToken(authHeader);
        log.info("Rejecting group: {} (Admin role required)", id);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<GroupDto>builder()
                            .success(false)
                            .message("Admin role required")
                            .error("FORBIDDEN")
                            .build());
        }

        GroupDto group = groupService.rejectGroup(id);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group rejected successfully")
                .data(group)
                .build());
    }

    // Member endpoints

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<GroupDto>>> getGroupMembers(@PathVariable Long id) {
        log.info("Fetching members for group: {}", id);
        List<GroupDto> members = groupService.getGroupMembers(id);
        return ResponseEntity.ok(ApiResponse.<List<GroupDto>>builder()
                .success(true)
                .message("Members fetched successfully")
                .data(members)
                .build());
    }

    // Helper methods

    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        return null;
    }

    private String extractUsernameFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUsername(token);
        }
        return null;
    }

    private String extractRoleFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractRole(token);
        }
        return null;
    }
}
