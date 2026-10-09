package com.fittrack.exception;

public class DuplicateEmailException extends ValidationException {

    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String email) {
        super("The email " + email + " is already registered.");
    }
}
