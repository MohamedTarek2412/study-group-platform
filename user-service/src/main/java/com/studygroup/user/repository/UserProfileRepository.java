package com.studygroup.user.repository;

import com.studygroup.user.model.CreatorStatus;
import com.studygroup.user.model.Role;
import com.studygroup.user.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link UserProfile} aggregate.
 *
 * <p>Only query methods that cannot be expressed as Spring Data
 * method names use {@code @Query} to keep the interface clean.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByAuthUserId(UUID authUserId);

    Optional<UserProfile> findByUsername(String username);

    Optional<UserProfile> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByAuthUserId(UUID authUserId);

    Page<UserProfile> findByRoleAndActiveTrue(Role role, Pageable pageable);

    Page<UserProfile> findByCreatorStatusAndActiveTrue(CreatorStatus status, Pageable pageable);

    /**
     * Full-text style search across display name, username, and bio.
     * Uses ILIKE for case-insensitive matching; switch to pg_trgm for production scale.
     */
    @Query("""
            SELECT u FROM UserProfile u
            WHERE u.active = true
              AND (
                  LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.username)    LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    Page<UserProfile> searchActiveProfiles(@Param("query") String query, Pageable pageable);

    /**
     * Finds active profiles that list a specific subject.
     * Uses MEMBER OF HQL for the subjects collection.
     */
    @Query("""
            SELECT u FROM UserProfile u
            WHERE u.active = true
              AND :subject MEMBER OF u.subjects
            """)
    Page<UserProfile> findBySubjectAndActiveTrue(@Param("subject") String subject, Pageable pageable);
}