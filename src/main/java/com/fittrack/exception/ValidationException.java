package com.fittrack.exception;

/**
 * Thrown when form input doesn't pass our checks. The message is shown
 * straight to the user, so keep it readable.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
