package com.studygroup.user.model;

/**
 * State machine for a user's creator-application lifecycle.
 *
 * <pre>
 *  NOT_APPLIED → PENDING → APPROVED
 *                       ↘ REJECTED → PENDING (can re-apply)
 * </pre>
 */
public enum CreatorStatus {
    NOT_APPLIED,
    PENDING,
    APPROVED,
    REJECTED
}