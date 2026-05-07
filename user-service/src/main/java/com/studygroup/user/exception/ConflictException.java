package com.studygroup.user.exception;

/**
 * Thrown when an operation would violate a business-level uniqueness or
 * state constraint (e.g., duplicate creator application, username taken).
 * Maps to HTTP 409 in {@link GlobalExceptionHandler}.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
