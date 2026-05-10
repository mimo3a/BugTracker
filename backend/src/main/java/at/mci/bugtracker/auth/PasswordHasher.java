package at.mci.bugtracker.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHasher {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);
    private PasswordHasher() {}

    public static String hash(String plain) {
        return ENCODER.encode(plain);
    }

    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null) {
            return false;
        }

        try {
            return ENCODER.matches(plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}