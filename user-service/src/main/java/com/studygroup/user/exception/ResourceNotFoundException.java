package com.studygroup.user.exception;

/**
 * Thrown when a requested resource cannot be found in the repository.
 * Maps to HTTP 404 in {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
