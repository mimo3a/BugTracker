package at.mci.bugtracker.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads the {@code session} cookie on every request, looks up the
 * matching {@link SessionStore.Session}, and (if present) installs an
 * {@link UsernamePasswordAuthenticationToken} into the
 * {@link SecurityContextHolder} with the session as the principal and
 * the user's role mapped to a {@code ROLE_<role>} {@link
 * SimpleGrantedAuthority}.
 */
@Component
public class SessionAuthFilter extends OncePerRequestFilter {
    public static final String COOKIE_NAME = "session";
    private final SessionStore sessions;

    public SessionAuthFilter(SessionStore sessions) {
        this.sessions = sessions;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        SessionStore.Session session = readSessionFromCookie(request);
        if (session != null) {
            var authority = new SimpleGrantedAuthority("ROLE_" + session.role());
            var auth = new UsernamePasswordAuthenticationToken(session, null, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }

    private SessionStore.Session readSessionFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                return sessions.get(c.getValue());
            }
        }
        return null;
    }
}
