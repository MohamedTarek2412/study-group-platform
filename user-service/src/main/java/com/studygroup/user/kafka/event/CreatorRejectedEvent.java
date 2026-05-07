package com.studygroup.user.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an admin rejects a creator application.
 * Topic: {@code user.creator.rejected}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorRejectedEvent {

    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    private UUID profileId;
    private UUID authUserId;
    private String username;
    private String email;
    private String reason;
    private String rejectedByAdminId;

    @Builder.Default
    private Instant occurredAt = Instant.now();
}
