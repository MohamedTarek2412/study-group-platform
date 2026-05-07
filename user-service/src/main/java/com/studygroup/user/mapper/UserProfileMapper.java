package com.studygroup.user.mapper;

import com.studygroup.user.dto.PublicProfileResponse;
import com.studygroup.user.dto.UserProfileResponse;
import com.studygroup.user.dto.UserProfileSummary;
import com.studygroup.user.kafka.event.CreatorApprovedEvent;
import com.studygroup.user.kafka.event.CreatorRejectedEvent;
import com.studygroup.user.kafka.event.UserProfileUpdatedEvent;
import com.studygroup.user.model.UserProfile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * MapStruct mapper for the {@link UserProfile} aggregate.
 *
 * <p>Using a shared mapper component (componentModel = "spring") so it is
 * injectable everywhere without manual instantiation — satisfying DI principle.
 *
 * <p>All mapping is explicit; MapStruct generates null-safe, compile-checked code.
 */
@Component
public class UserProfileMapper {

    // ─── Entity → Response DTOs ──────────────────────────────────────────

    public UserProfileResponse toResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return UserProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .role(profile.getRole())
                .creatorStatus(profile.getCreatorStatus())
                .subjects(profile.getSubjects())
                .profileComplete(profile.isProfileComplete())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public PublicProfileResponse toPublicResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return PublicProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .role(profile.getRole())
                .subjects(profile.getSubjects())
                .build();
    }

    public UserProfileSummary toSummary(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return UserProfileSummary.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .role(profile.getRole())
                .creatorStatus(profile.getCreatorStatus())
                .build();
    }

    // ─── Entity → Kafka Events ───────────────────────────────────────────

    public UserProfileUpdatedEvent toProfileUpdatedEvent(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return UserProfileUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .profileId(profile.getId())
                .authUserId(profile.getAuthUserId())
                .username(profile.getUsername())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .subjects(profile.getSubjects())
                .occurredAt(Instant.now())
                .build();
    }

    public CreatorApprovedEvent toCreatorApprovedEvent(UserProfile profile, String adminId) {
        if (profile == null) {
            return null;
        }
        return CreatorApprovedEvent.builder()
                .eventId(UUID.randomUUID())
                .profileId(profile.getId())
                .authUserId(profile.getAuthUserId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .approvedByAdminId(adminId)
                .occurredAt(Instant.now())
                .build();
    }

    public CreatorRejectedEvent toCreatorRejectedEvent(UserProfile profile, String reason, String adminId) {
        if (profile == null) {
            return null;
        }
        return CreatorRejectedEvent.builder()
                .eventId(UUID.randomUUID())
                .profileId(profile.getId())
                .authUserId(profile.getAuthUserId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .reason(reason)
                .rejectedByAdminId(adminId)
                .occurredAt(Instant.now())
                .build();
    }

    // ─── Partial Update (PATCH semantics) ────────────────────────────────

    /**
     * Applies non-null fields from a source profile onto an existing profile.
     * Used by the update flow; preserves fields the caller did not send.
     */
    public void mergeIntoProfile(UserProfile source, UserProfile target) {
        if (source == null || target == null) {
            return;
        }
        if (source.getDisplayName() != null) target.setDisplayName(source.getDisplayName());
        if (source.getBio() != null) target.setBio(source.getBio());
        if (source.getAvatarUrl() != null) target.setAvatarUrl(source.getAvatarUrl());
        if (source.getSubjects() != null) target.setSubjects(source.getSubjects());
        if (source.getRole() != null) target.setRole(source.getRole());
        if (source.getCreatorStatus() != null) target.setCreatorStatus(source.getCreatorStatus());
    }
}
