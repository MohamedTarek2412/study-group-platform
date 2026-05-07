package com.studygroup.user.controller;

import com.studygroup.user.dto.*;
import com.studygroup.user.model.CreatorStatus;
import com.studygroup.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for User Profile operations.
 *
 * <p>Thin controller layer: validates input, delegates to {@link UserService},
 * and maps results to HTTP responses. No business logic lives here.
 *
 * <p>Base path: {@code /api/v1/users}
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // ─── Self (Authenticated User) ────────────────────────────────────────

    /**
     * GET /api/v1/users/me
     * Returns the full profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UUID authUserId) {
        return ResponseEntity.ok(userService.getMyProfile(authUserId));
    }

    /**
     * PATCH /api/v1/users/me
     * Partially updates the authenticated user's profile.
     */
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UUID authUserId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(authUserId, request));
    }

    /**
     * POST /api/v1/users/me/creator-application
     * Submits a creator-status application for the authenticated STUDENT.
     */
    @PostMapping("/me/creator-application")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserProfileResponse> applyForCreator(
            @AuthenticationPrincipal UUID authUserId) {
        return ResponseEntity.ok(userService.applyForCreatorStatus(authUserId));
    }

    // ─── Public Profile Browsing ──────────────────────────────────────────

    /**
     * GET /api/v1/users/{username}
     * Public view of a user's profile — no authentication required.
     */
    @GetMapping("/{username}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicProfileByUsername(username));
    }

    /**
     * GET /api/v1/users/profile/{profileId}
     * Lookup by internal profile UUID (used by inter-service calls via gateway).
     */
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<PublicProfileResponse> getProfileById(
            @PathVariable UUID profileId) {
        return ResponseEntity.ok(userService.getPublicProfile(profileId));
    }

    /**
     * GET /api/v1/users/search?q=...&page=0&size=20
     * Full-text search across username and display name.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserProfileSummary>> searchProfiles(
            @RequestParam(name = "q", defaultValue = "") String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.searchProfiles(query, pageable));
    }

    // ─── Admin Operations ─────────────────────────────────────────────────

    /**
     * GET /api/v1/admin/users/creator-applications?status=PENDING
     * Lists creator applications filtered by status. ADMIN only.
     */
    @GetMapping("/admin/creator-applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileSummary>> getCreatorApplications(
            @RequestParam(defaultValue = "PENDING") CreatorStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getProfilesByCreatorStatus(status, pageable));
    }

    /**
     * POST /api/v1/admin/users/{profileId}/creator-application/decision
     * Approve or reject a creator application. ADMIN only.
     */
    @PostMapping("/admin/{profileId}/creator-application/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> processCreatorApplication(
            @PathVariable UUID profileId,
            @Valid @RequestBody CreatorApprovalRequest request,
            @AuthenticationPrincipal UUID adminAuthUserId) {
        return ResponseEntity.ok(
                userService.processCreatorApplication(profileId, request, adminAuthUserId));
    }

    /**
     * DELETE /api/v1/admin/users/{profileId}
     * Soft-deletes (deactivates) a user profile. ADMIN only.
     */
    @DeleteMapping("/admin/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateProfile(
            @PathVariable UUID profileId,
            @AuthenticationPrincipal UUID adminAuthUserId) {
        userService.deactivateProfile(profileId, adminAuthUserId);
        return ResponseEntity.noContent().build();
    }
}
