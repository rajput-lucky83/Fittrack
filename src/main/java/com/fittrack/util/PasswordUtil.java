package com.fittrack.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Salted SHA-256. Stored format is  salt:hash  (both Base64).
 * (A real production app should use bcrypt/argon2, but this keeps the
 * project free of extra libraries.)
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() { }

    public static String hash(String rawPassword) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + digest(salt, rawPassword);
    }

    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null || !stored.contains(":")) {
            return false;
        }
        String[] parts = stored.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        return digest(salt, rawPassword).equals(parts[1]);
    }

    private static String digest(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] out = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (NoSuchAlgorithmException e) {
            // every JVM has SHA-256, so this really shouldn't happen
            throw new IllegalStateException(e);
        }
    }
}
