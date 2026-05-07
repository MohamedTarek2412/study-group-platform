package com.studygroup.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * Payload for updating a user's own profile.
 * All fields are optional — caller sends only what changes (PATCH semantics).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    @Pattern(regexp = "^(https?://).*", message = "Avatar URL must be a valid HTTP/HTTPS URL")
    private String avatarUrl;

    @Size(max = 10, message = "You may select at most 10 subjects")
    private List<@NotBlank @Size(max = 50) String> subjects;
}