package com.studygroup.user.kafka.event;

import com.studygroup.user.model.Role;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// ═══════════════════════════════════════════════════════════════════════════
//  UserProfileUpdatedEvent
//  Topic: user.profile.updated
//  Published when a user saves changes to their profile.
// ═══════════════════════════════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdatedEvent {

    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    private UUID profileId;
    private UUID authUserId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private List<String> subjects;

    @Builder.Default
    private Instant occurredAt = Instant.now();
}
