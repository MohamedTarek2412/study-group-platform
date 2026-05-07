package com.studygroup.user.service;

import com.studygroup.user.dto.*;
import com.studygroup.user.kafka.event.UserRegisteredEvent;
import com.studygroup.user.model.CreatorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service contract for the User Profile bounded context.
 *
 * <p>Segregated into fine-grained operations following the Interface
 * Segregation Principle — callers depend only on the methods they use.
 */
public interface UserService {

    /**
     * Bootstrap a profile when auth-service reports a new registration.
     * Idempotent — safe to call multiple times with the same event.
     */
    void handleUserRegistered(UserRegisteredEvent event);

    /**
     * Retrieve the full profile of the authenticated user (self-view).
     *
     * @param authUserId the caller's identity from the JWT
     */
    UserProfileResponse getMyProfile(UUID authUserId);

    /**
     * Retrieve the public-facing view of any user by their profile ID.
     */
    PublicProfileResponse getPublicProfile(UUID profileId);

    /**
     * Retrieve the public-facing view by username (for /users/{username} routes).
     */
    PublicProfileResponse getPublicProfileByUsername(String username);

    /**
     * Partially update the authenticated user's own profile (PATCH semantics).
     *
     * @param authUserId caller's identity
     * @param request    fields to update (nulls are ignored)
     */
    UserProfileResponse updateProfile(UUID authUserId, UpdateProfileRequest request);

    /**
     * Submit an application to become a content creator.
     * Only STUDENTs with no pending application may call this.
     */
    UserProfileResponse applyForCreatorStatus(UUID authUserId);

    /**
     * Admin action: approve or reject a creator application.
     *
     * @param targetProfileId the profile being reviewed
     * @param request         APPROVE or REJECT + optional reason
     * @param adminAuthUserId the admin's identity (for audit trail)
     */
    UserProfileResponse processCreatorApplication(
            UUID targetProfileId,
            CreatorApprovalRequest request,
            UUID adminAuthUserId
    );

    /**
     * Paginated search for active profiles by partial name or username.
     */
    Page<UserProfileSummary> searchProfiles(String query, Pageable pageable);

    /**
     * Paginated list of profiles filtered by creator application status (admin only).
     */
    Page<UserProfileSummary> getProfilesByCreatorStatus(CreatorStatus status, Pageable pageable);

    /**
     * Soft-delete (deactivate) a profile. Admin-only.
     */
    void deactivateProfile(UUID profileId, UUID adminAuthUserId);
}
