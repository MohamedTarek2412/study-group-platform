package com.studygroup.user.service.impl;

import com.studygroup.user.dto.*;
import com.studygroup.user.exception.ConflictException;
import com.studygroup.user.exception.ResourceNotFoundException;
import com.studygroup.user.kafka.event.UserRegisteredEvent;
import com.studygroup.user.kafka.producer.UserEventProducer;
import com.studygroup.user.mapper.UserProfileMapper;
import com.studygroup.user.model.*;
import com.studygroup.user.repository.UserProfileRepository;
import com.studygroup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Core implementation of the {@link UserService} contract.
 *
 * <p>Single Responsibility: orchestrates profile CRUD and the creator
 * approval workflow. All persistence goes through the repository;
 * all cross-service communication goes through the Kafka producer.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // default read-only; mutations override explicitly
public class UserServiceImpl implements UserService {

    private final UserProfileRepository profileRepository;
    private final UserProfileMapper     mapper;
    private final UserEventProducer     eventProducer;

    // ─── Event-Driven Profile Bootstrap ──────────────────────────────────

    @Override
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Idempotency guard: do nothing if profile already exists for this auth user
        if (profileRepository.existsByAuthUserId(event.getAuthUserId())) {
            log.warn("Profile already exists for authUserId={}; skipping duplicate event eventId={}",
                    event.getAuthUserId(), event.getEventId());
            return;
        }

        log.info("Bootstrapping profile for authUserId={} username={}",
                event.getAuthUserId(), event.getUsername());

        UserProfile profile = UserProfile.builder()
                .authUserId(event.getAuthUserId())
                .username(event.getUsername())
                .email(event.getEmail())
                .displayName(event.getUsername()) // sensible default; user updates later
                .role(Role.STUDENT)
                .creatorStatus(CreatorStatus.NOT_APPLIED)
                .build();

        profileRepository.save(profile);
        log.info("Profile created profileId={} for authUserId={}", profile.getId(), event.getAuthUserId());
    }

    // ─── Profile Reads ────────────────────────────────────────────────────

    @Override
    public UserProfileResponse getMyProfile(UUID authUserId) {
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        return mapper.toResponse(profile);
    }

    @Override
    public PublicProfileResponse getPublicProfile(UUID profileId) {
        UserProfile profile = findByIdOrThrow(profileId);
        return mapper.toPublicResponse(profile);
    }

    @Override
    public PublicProfileResponse getPublicProfileByUsername(String username) {
        UserProfile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserProfile", "username", username));
        return mapper.toPublicResponse(profile);
    }

    // ─── Profile Updates ─────────────────────────────────────────────────

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UUID authUserId, UpdateProfileRequest request) {
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);

        applyUpdates(profile, request);
        profile.recalculateCompleteness();

        UserProfile saved = profileRepository.save(profile);

        // Publish event so downstream services (e.g., content-service) stay in sync
        eventProducer.publishProfileUpdated(mapper.toProfileUpdatedEvent(saved));

        log.info("Profile updated for authUserId={}", authUserId);
        return mapper.toResponse(saved);
    }

    // ─── Creator Workflow ─────────────────────────────────────────────────

    @Override
    @Transactional
    public UserProfileResponse applyForCreatorStatus(UUID authUserId) {
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);

        // domain method encapsulates the state-transition rules
        profile.applyForCreator();

        UserProfile saved = profileRepository.save(profile);
        log.info("Creator application submitted for profileId={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserProfileResponse processCreatorApplication(
            UUID targetProfileId,
            CreatorApprovalRequest request,
            UUID adminAuthUserId) {

        UserProfile profile = findByIdOrThrow(targetProfileId);

        if (profile.getCreatorStatus() != CreatorStatus.PENDING) {
            throw new ConflictException(
                    "Cannot process application: status is " + profile.getCreatorStatus());
        }

        String adminIdStr = adminAuthUserId.toString();

        switch (request.getDecision()) {
            case APPROVE -> {
                profile.approveCreator();
                eventProducer.publishCreatorApproved(
                        mapper.toCreatorApprovedEvent(profile, adminIdStr));
                log.info("Creator APPROVED profileId={} by admin={}", targetProfileId, adminAuthUserId);
            }
            case REJECT -> {
                String reason = request.getReason() != null ? request.getReason() : "No reason provided";
                profile.rejectCreator(reason);
                eventProducer.publishCreatorRejected(
                        mapper.toCreatorRejectedEvent(profile, reason, adminIdStr));
                log.info("Creator REJECTED profileId={} by admin={} reason={}",
                        targetProfileId, adminAuthUserId, reason);
            }
        }

        return mapper.toResponse(profileRepository.save(profile));
    }

    // ─── Search & Listing ─────────────────────────────────────────────────

    @Override
    public Page<UserProfileSummary> searchProfiles(String query, Pageable pageable) {
        return profileRepository
                .searchActiveProfiles(query, pageable)
                .map(mapper::toSummary);
    }

    @Override
    public Page<UserProfileSummary> getProfilesByCreatorStatus(
            CreatorStatus status, Pageable pageable) {
        return profileRepository
                .findByCreatorStatusAndActiveTrue(status, pageable)
                .map(mapper::toSummary);
    }

    // ─── Admin Operations ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void deactivateProfile(UUID profileId, UUID adminAuthUserId) {
        UserProfile profile = findByIdOrThrow(profileId);
        profile.deactivate();
        profileRepository.save(profile);
        log.warn("Profile DEACTIVATED profileId={} by admin={}", profileId, adminAuthUserId);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────

    private UserProfile findByIdOrThrow(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "id", id));
    }

    private UserProfile findByAuthUserIdOrThrow(UUID authUserId) {
        return profileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserProfile", "authUserId", authUserId));
    }

    /**
     * Applies non-null request fields onto the entity (PATCH semantics).
     * Adding update logic here keeps the entity free of DTO awareness.
     */
    private void applyUpdates(UserProfile profile, UpdateProfileRequest req) {
        if (req.getDisplayName() != null) profile.setDisplayName(req.getDisplayName());
        if (req.getBio()         != null) profile.setBio(req.getBio());
        if (req.getAvatarUrl()   != null) profile.setAvatarUrl(req.getAvatarUrl());
        if (req.getSubjects()    != null) {
            profile.getSubjects().clear();
            profile.getSubjects().addAll(req.getSubjects());
        }
    }
}
