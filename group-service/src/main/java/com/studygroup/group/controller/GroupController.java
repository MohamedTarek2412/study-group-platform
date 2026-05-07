package com.studygroup.group.controller;

import com.studygroup.group.dto.ApiResponse;
import com.studygroup.group.dto.CreateGroupRequest;
import com.studygroup.group.dto.GroupDto;
import com.studygroup.group.dto.GroupMemberDto;
import com.studygroup.group.security.AuthenticatedUser;
import com.studygroup.group.security.SecurityUtils;
import com.studygroup.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;

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
    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    public ResponseEntity<ApiResponse<GroupDto>> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Long creatorId = currentUser.getUserId();
        String creatorName = currentUser.getUsername();
        log.info("Creating group for creator: {} ({})", creatorId, creatorName);

        GroupDto group = groupService.createGroup(creatorId, creatorName, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<GroupDto>builder()
                        .success(true)
                        .message("Group created successfully")
                        .data(group)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    public ResponseEntity<ApiResponse<GroupDto>> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody CreateGroupRequest request) {
        Long creatorId = SecurityUtils.getCurrentUser().getUserId();
        log.info("Updating group {} for creator: {}", id, creatorId);

        GroupDto group = groupService.updateGroup(id, creatorId, request);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group updated successfully")
                .data(group)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteGroup(
            @PathVariable Long id) {
        Long creatorId = SecurityUtils.getCurrentUser().getUserId();
        log.info("Deleting group {} for creator: {}", id, creatorId);

        groupService.deleteGroup(id, creatorId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Group deleted successfully")
                .build());
    }

    // Admin endpoints

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GroupDto>> approveGroup(
            @PathVariable Long id) {
        log.info("Approving group: {} (Admin role required)", id);

        GroupDto group = groupService.approveGroup(id);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group approved successfully")
                .data(group)
                .build());
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GroupDto>> rejectGroup(
            @PathVariable Long id) {
        log.info("Rejecting group: {} (Admin role required)", id);

        GroupDto group = groupService.rejectGroup(id);
        return ResponseEntity.ok(ApiResponse.<GroupDto>builder()
                .success(true)
                .message("Group rejected successfully")
                .data(group)
                .build());
    }

    // Member endpoints

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<GroupMemberDto>>> getGroupMembers(@PathVariable Long id) {
        log.info("Fetching members for group: {}", id);
        List<GroupMemberDto> members = groupService.getGroupMembers(id);
        return ResponseEntity.ok(ApiResponse.<List<GroupMemberDto>>builder()
                .success(true)
                .message("Members fetched successfully")
                .data(members)
                .build());
    }
}
