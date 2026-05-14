package at.mci.bugtracker.auth;

import at.mci.bugtracker.model.UserRole;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class SessionStore {
    public record Session(long userId, String username, UserRole role) {}

    private static final SecureRandom RNG = new SecureRandom();
    private final ConcurrentHashMap<String, Session> store = new ConcurrentHashMap<>();

    public String create(long userId, String username, UserRole role) {
        String token = generateToken();
        store.put(token, new Session(userId, username, role));
        return token;
    }

    public Session get(String token) {
        if (token == null) return null;
        return store.get(token);
    }

    public void delete(String token) {
        if (token == null) return;
        store.remove(token);
    }

    private static String generateToken() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        return HexFormat.of().formatHex(buf);
    }
}
