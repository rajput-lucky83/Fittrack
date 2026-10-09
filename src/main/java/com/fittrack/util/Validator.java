package com.fittrack.util;

import com.fittrack.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Small helpers so servlets don't repeat the same checks. */
public final class Validator {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validator() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String required(String value, String fieldName) throws ValidationException {
        if (isBlank(value)) {
            throw new ValidationException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static String email(String value) throws ValidationException {
        String v = required(value, "Email").toLowerCase();
        if (!EMAIL.matcher(v).matches()) {
            throw new ValidationException("Please enter a valid email address.");
        }
        return v;
    }

    public static String password(String value) throws ValidationException {
        if (value == null || value.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters.");
        }
        return value;
    }

    public static int positiveInt(String value, String fieldName, int max) throws ValidationException {
        int n;
        try {
            n = Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a whole number.");
        }
        if (n <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
        if (n > max) {
            throw new ValidationException(fieldName + " cannot be more than " + max + ".");
        }
        return n;
    }

    /** blank means "not given", which is fine for optional measurements */
    public static Double optionalDecimal(String value, String fieldName, double min, double max)
            throws ValidationException {
        if (isBlank(value)) return null;
        double d;
        try {
            d = Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a number.");
        }
        if (d < min || d > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max + ".");
        }
        return d;
    }

    public static LocalDate date(String value, String fieldName) throws ValidationException {
        try {
            return LocalDate.parse(value == null ? "" : value.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Please choose a valid " + fieldName.toLowerCase() + ".");
        }
    }

    public static String maxLength(String value, int max, String fieldName) throws ValidationException {
        if (value != null && value.length() > max) {
            throw new ValidationException(fieldName + " is too long (max " + max + " characters).");
        }
        return value;
    }
}
