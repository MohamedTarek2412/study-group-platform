package com.studygroup.user.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Inbound event published by auth-service when a new user registers.
 * Triggers profile bootstrapping in this service.
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

    /** Correlation ID matching auth-service's user record. */
    private UUID authUserId;

    /** Email address of the registered user. */
    private String email;

    /** User role assigned at registration (ADMIN, USER, CREATOR). */
    private String role;

    /** ISO-8601 timestamp from the auth-service. */
    @Builder.Default
    private Instant registeredAt = Instant.now();

    /** Source service identifier. */
    @Builder.Default
    private String source = "auth-service";
}

