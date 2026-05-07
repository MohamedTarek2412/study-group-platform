package com.studygroup.auth.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a new user successfully registers.
 * Consumed by user-service to bootstrap user profiles.
 *
 * <p>Topic: {@code auth.user-registered}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisteredEvent {

    /** Unique ID for this event (idempotency key). */
    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    /** Unique ID of the newly registered auth user. */
    private UUID authUserId;

    /** Email address of the registered user. */
    private String email;

    /** User role assigned at registration (ADMIN, USER, CREATOR). */
    private String role;

    /** ISO-8601 timestamp when the event was created. */
    @Builder.Default
    private Instant registeredAt = Instant.now();

    /** Source service identifier. */
    @Builder.Default
    private String source = "auth-service";
}
