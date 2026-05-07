package com.studygroup.user.dto;

import com.studygroup.user.model.CreatorStatus;
import com.studygroup.user.model.Role;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Full profile view — returned to the profile owner or an ADMIN.
 * Includes email and internal creator workflow state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private Role role;
    private CreatorStatus creatorStatus;
    private List<String> subjects;
    private boolean profileComplete;
    private Instant createdAt;
    private Instant updatedAt;
}