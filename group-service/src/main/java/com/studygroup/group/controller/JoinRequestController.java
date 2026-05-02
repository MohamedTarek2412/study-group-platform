package com.studygroup.group.controller;

import com.studygroup.group.dto.ApiResponse;
import com.studygroup.group.dto.CreateJoinRequestDto;
import com.studygroup.group.dto.JoinRequestDto;
import com.studygroup.group.service.JoinRequestService;
import com.studygroup.group.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/join-requests")
@RequiredArgsConstructor
@Slf4j
public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<JoinRequestDto>> createJoinRequest(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateJoinRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        String userName = extractUsernameFromToken(authHeader);
        log.info("Creating join request for user {} to group {}", userId, groupId);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<JoinRequestDto>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        JoinRequestDto joinRequest = joinRequestService.createJoinRequest(groupId, userId, userName, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<JoinRequestDto>builder()
                        .success(true)
                        .message("Join request created successfully")
                        .data(joinRequest)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JoinRequestDto>>> getJoinRequests(
            @PathVariable Long groupId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        log.info("Fetching join requests for group: {} by user: {}", groupId, creatorId);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<List<JoinRequestDto>>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        List<JoinRequestDto> joinRequests = joinRequestService.getPendingJoinRequestsForGroup(groupId);
        return ResponseEntity.ok(ApiResponse.<List<JoinRequestDto>>builder()
                .success(true)
                .message("Join requests fetched successfully")
                .data(joinRequests)
                .build());
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ApiResponse<JoinRequestDto>> acceptJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        log.info("Accepting join request {} for group {} by user: {}", requestId, groupId, creatorId);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<JoinRequestDto>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        JoinRequestDto joinRequest = joinRequestService.acceptJoinRequest(groupId, requestId, creatorId);
        return ResponseEntity.ok(ApiResponse.<JoinRequestDto>builder()
                .success(true)
                .message("Join request accepted successfully")
                .data(joinRequest)
                .build());
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<JoinRequestDto>> rejectJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long creatorId = extractUserIdFromToken(authHeader);
        log.info("Rejecting join request {} for group {} by user: {}", requestId, groupId, creatorId);

        if (creatorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<JoinRequestDto>builder()
                            .success(false)
                            .message("Authentication required")
                            .error("UNAUTHORIZED")
                            .build());
        }

        JoinRequestDto joinRequest = joinRequestService.rejectJoinRequest(groupId, requestId, creatorId);
        return ResponseEntity.ok(ApiResponse.<JoinRequestDto>builder()
                .success(true)
                .message("Join request rejected successfully")
                .data(joinRequest)
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
}
