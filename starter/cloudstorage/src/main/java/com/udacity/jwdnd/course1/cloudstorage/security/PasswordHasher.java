package com.udacity.jwdnd.course1.cloudstorage.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;     // 128-bit salt
    private static final int ITERATIONS = 120_000; // educational but modern-ish
    private static final int KEY_LENGTH = 256;    // bits

    private static final SecureRandom RNG = new SecureRandom();

    public static String newSaltBase64() {
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashBase64(char[] password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }
}