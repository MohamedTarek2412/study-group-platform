package com.studygroup.group.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final Long userId;
    private final String username;
    private final String role;
}
