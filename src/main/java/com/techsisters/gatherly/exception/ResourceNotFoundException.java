package com.techsisters.gatherly.exception;

/**
 * Throw from a service when the requested thing doesn't exist.
 * GlobalExceptionHandler turns it into a 404 with a ResponseDTO body.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
