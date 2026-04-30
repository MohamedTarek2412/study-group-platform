package com.studygroup.auth.exception;

/**
 * Thrown when there is a conflict (e.g., duplicate email registration).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
