package com.fittrack.model;

public enum Role {
    ADMIN("/admin/dashboard"),
    USER("/user/dashboard");

    private final String homePath;

    Role(String homePath) {
        this.homePath = homePath;
    }

    public String getHomePath() {
        return homePath;
    }

    public static Role fromString(String s) {
        if (s == null) return USER;
        try {
            return Role.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
