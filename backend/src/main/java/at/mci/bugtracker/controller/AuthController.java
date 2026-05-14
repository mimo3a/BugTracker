package at.mci.bugtracker.controller;

import at.mci.bugtracker.model.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.PasswordHasher;
import at.mci.bugtracker.auth.SessionAuthFilter;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.util.HttpException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AuthController {
    private final UserDao users;
    private final SessionStore sessions;

    public AuthController(UserDao users, SessionStore sessions) {
        this.users = users;
        this.sessions = sessions;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<UserWithoutHash> register(@Valid @RequestBody Requests.Register body, HttpServletResponse response) {
        if (!body.password().equals(body.passwordConfirm())) {
            throw new HttpException(400, "Passwörter stimmen nicht überein");
        }

        if (users.existsByUsernameOrEmail(body.username(), body.email())) {
            throw new HttpException(409, "Username or email already taken");
        }

        UserRole role = UserRole.TESTER;    // Role on registration: TESTER
        String hash = PasswordHasher.hash(body.password());
        User u = users.save(body.username(), body.email(), hash, role);
        setSessionCookie(response, sessions.create(u.id(), u.username(), u.role()));

        return ResponseEntity.status(201).body(
                new UserWithoutHash(u.id(), u.username(), u.email(), u.role(), u.active(), u.createdAt())
        );
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<UserWithoutHash> login(@Valid @RequestBody Requests.Login body, HttpServletResponse response) {
        User user = users.findByUsername(body.username());

        if (user == null || !PasswordHasher.verify(body.password(), user.passwordHash())) {
            throw new HttpException(401, "Invalid username or password");
        }

        if (!user.active()) {
            throw new HttpException(403, "Account ist deaktiviert");
        }

        setSessionCookie(response, sessions.create(user.id(), user.username(), user.role()));
        return ResponseEntity.ok(new UserWithoutHash(user.id(), user.username(), user.email(), user.role(), user.active(), user.createdAt()));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = SessionAuthFilter.COOKIE_NAME, required = false) String token,
                                       HttpServletResponse response) {
        sessions.delete(token);
        clearSessionCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/auth/me")
    public Me me() {
        SessionStore.Session s = CurrentSession.optional();
        if (s == null) throw new HttpException(401, "Not logged in");
        return new Me(s.userId(), s.username(), s.role());
    }

    @PostMapping("/api/auth/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody Requests.ChangePassword body) {
        SessionStore.Session s = CurrentSession.require();
        User uh = users.findById(s.userId());
        if (uh == null) throw new HttpException(404, "User not found");
        if (!PasswordHasher.verify(body.currentPassword(), uh.passwordHash())) {
            throw new HttpException(401, "Current password is incorrect");
        }
        users.updatePassword(s.userId(), PasswordHasher.hash(body.newPassword()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users")
    public Object listUsers() {
        CurrentSession.require();
        return users.findAll();
    }

    private static void setSessionCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(SessionAuthFilter.COOKIE_NAME, token)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private static void clearSessionCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SessionAuthFilter.COOKIE_NAME, "")
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
