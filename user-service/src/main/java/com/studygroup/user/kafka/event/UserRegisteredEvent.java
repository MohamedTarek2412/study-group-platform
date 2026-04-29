package com.studygroup.user.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Inbound event published by auth-service when a new user registers.
 * Triggers profile bootstrapping in this service.
 *
 * <p>Topic: {@code user.registered}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisteredEvent {

    /** Correlation ID matching auth-service's user record. */
    private UUID authUserId;

    private String username;
    private String email;

    /** ISO-8601 timestamp from the auth-service. */
    private Instant registeredAt;

    /** Unique ID for this event (idempotency key). */
    @Builder.Default
    private UUID eventId = UUID.randomUUID();
}
