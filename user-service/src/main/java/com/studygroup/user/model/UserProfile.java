package com.studygroup.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core aggregate root for the User bounded context.
 *
 * <p>Intentionally stores only PROFILE data — credentials live in auth-service.
 * The {@code authUserId} field is the cross-service correlation key.
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"bio", "avatarUrl"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Logical foreign key to auth-service's user table.
     * Never exposed in outbound DTOs — used internally for cross-service correlation.
     */
    @Column(name = "auth_user_id", nullable = false, unique = true, updatable = false)
    private UUID authUserId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private Role role = Role.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "creator_status", nullable = false, length = 30)
    @Builder.Default
    private CreatorStatus creatorStatus = CreatorStatus.NOT_APPLIED;

    /**
     * Subject tags (e.g. "Mathematics", "Physics").
     * Stored as a PostgreSQL text array via JPA converter.
     */
    @ElementCollection
    @CollectionTable(
            name = "user_profile_subjects",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "subject")
    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    @Column(name = "profile_complete", nullable = false)
    @Builder.Default
    private boolean profileComplete = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ─── Domain Behaviour ────────────────────────────────────────────────────

    /**
     * Marks the profile as complete when all required fields are populated.
     * Called after an update; keeps completeness logic in the domain.
     */
    public void recalculateCompleteness() {
        this.profileComplete = displayName != null && !displayName.isBlank()
                && bio != null && !bio.isBlank()
                && avatarUrl != null && !avatarUrl.isBlank()
                && subjects != null && !subjects.isEmpty();
    }

    /** Soft-delete: marks the profile inactive instead of removing the row. */
    public void deactivate() {
        this.active = false;
    }

    /** Apply for creator status — only STUDENTS may apply. */
    public void applyForCreator() {
        if (this.role != Role.STUDENT) {
            throw new IllegalStateException("Only STUDENT users can apply for creator status.");
        }
        if (this.creatorStatus == CreatorStatus.PENDING) {
            throw new IllegalStateException("A creator application is already pending.");
        }
        this.creatorStatus = CreatorStatus.PENDING;
    }

    public void approveCreator() {
        this.creatorStatus = CreatorStatus.APPROVED;
        this.role = Role.CREATOR;
    }

    public void rejectCreator(String reason) {
        this.creatorStatus = CreatorStatus.REJECTED;
        // reason is communicated via event; not persisted here to keep entity lean
    }
}