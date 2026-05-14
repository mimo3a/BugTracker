package at.mci.bugtracker.auth;

import at.mci.bugtracker.util.HttpException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience accessor for the {@link SessionStore.Session} that
 * {@link SessionAuthFilter} put into the SecurityContext for the current
 * request. Avoids sprinkling SecurityContextHolder.getContext()...
 * across the codebase.
 */
public final class CurrentSession {

    private CurrentSession() {}

    public static SessionStore.Session require() {
        SessionStore.Session s = optional();

        if (s == null) {
            throw new HttpException(401, "Unauthorized");
        }

        return s;
    }

    public static SessionStore.Session optional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        return principal instanceof SessionStore.Session s ? s : null;
    }
}