package com.studygroup.group.controller;

import com.studygroup.group.dto.ApiResponse;
import com.studygroup.group.dto.CreateJoinRequestDto;
import com.studygroup.group.dto.JoinRequestDto;
import com.studygroup.group.security.AuthenticatedUser;
import com.studygroup.group.security.SecurityUtils;
import com.studygroup.group.service.JoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/join-requests")
@RequiredArgsConstructor
@Slf4j
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<JoinRequestDto>> createJoinRequest(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateJoinRequestDto request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Long userId = currentUser.getUserId();
        String userName = currentUser.getUsername();
        log.info("Creating join request for user {} to group {}", userId, groupId);

        JoinRequestDto joinRequest = joinRequestService.createJoinRequest(groupId, userId, userName, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<JoinRequestDto>builder()
                        .success(true)
                        .message("Join request created successfully")
                        .data(joinRequest)
                        .build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<JoinRequestDto>>> getJoinRequests(
            @PathVariable Long groupId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Long userId = currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        log.info("Fetching join requests for group: {} by user: {}", groupId, userId);

        List<JoinRequestDto> joinRequests = joinRequestService.getPendingJoinRequestsForGroup(groupId, userId, isAdmin);
        return ResponseEntity.ok(ApiResponse.<List<JoinRequestDto>>builder()
                .success(true)
                .message("Join requests fetched successfully")
                .data(joinRequests)
                .build());
    }

    @PutMapping("/{requestId}/accept")
    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    public ResponseEntity<ApiResponse<JoinRequestDto>> acceptJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Long requesterId = currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        log.info("Accepting join request {} for group {} by user: {}", requestId, groupId, requesterId);

        JoinRequestDto joinRequest = joinRequestService.acceptJoinRequest(groupId, requestId, requesterId, isAdmin);
        return ResponseEntity.ok(ApiResponse.<JoinRequestDto>builder()
                .success(true)
                .message("Join request accepted successfully")
                .data(joinRequest)
                .build());
    }

    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
    public ResponseEntity<ApiResponse<JoinRequestDto>> rejectJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Long requesterId = currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        log.info("Rejecting join request {} for group {} by user: {}", requestId, groupId, requesterId);

        JoinRequestDto joinRequest = joinRequestService.rejectJoinRequest(groupId, requestId, requesterId, isAdmin);
        return ResponseEntity.ok(ApiResponse.<JoinRequestDto>builder()
                .success(true)
                .message("Join request rejected successfully")
                .data(joinRequest)
                .build());
    }
}
