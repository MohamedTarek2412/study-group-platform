package com.studygroup.user.dto;

import com.studygroup.user.model.CreatorStatus;
import com.studygroup.user.model.Role;
import lombok.*;

import java.util.UUID;

/**
 * Summary row used in paginated lists (search results, admin panels, etc.).
 * Keeps payload small when full profile detail is not needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileSummary {
    private UUID id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private Role role;
    private CreatorStatus creatorStatus;
}