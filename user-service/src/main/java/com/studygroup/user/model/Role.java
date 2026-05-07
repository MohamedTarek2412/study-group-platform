package com.studygroup.user.model;

/**
 * RBAC roles for the StudyGroup platform.
 *
 * <p>Roles are hierarchical in terms of privilege but stored independently
 * to avoid tight coupling between role logic and service logic.
 */
public enum Role {
    STUDENT,
    CREATOR,
    ADMIN
}