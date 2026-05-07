package com.studygroup.user.dto;

import com.studygroup.user.model.Role;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Lightweight public view of a user — returned when browsing other people's profiles.
 * Intentionally omits email, creator-status internals, and timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicProfileResponse {
    private UUID id;
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private Role role;
    private List<String> subjects;
}