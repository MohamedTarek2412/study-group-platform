package com.studygroup.user.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

// ═══════════════════════════════════════════════════════════════════════════
//  CreatorApprovedEvent
//  Topic: user.creator.approved
//  Published when an admin approves a creator application.
// ═══════════════════════════════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorApprovedEvent {

    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    private UUID profileId;
    private UUID authUserId;
    private String username;
    private String email;
    private String approvedByAdminId;

    @Builder.Default
    private Instant occurredAt = Instant.now();
}
