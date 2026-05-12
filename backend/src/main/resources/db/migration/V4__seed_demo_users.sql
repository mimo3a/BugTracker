-- Seed-Accounts für Team-Demo + Beta-Vorführung.
-- Drei Rollen abgedeckt damit jedes FA-04/FA-06-Szenario testbar ist.
--
-- Logins:
--   admin   / admin123   → ADMIN
--   dev     / admin123   → DEVELOPER
--   tester  / admin123   → TESTER
--
-- Hash-Quelle: BCrypt-Encoder aus at.mci.bugtracker.auth.PasswordHasher
-- (BCryptPasswordEncoder, strength=10). Alle drei nutzen denselben Hash,
-- weil das Passwort gleich ist — der Salt ist Teil des Hash-Strings,
-- BCrypt.verify() prüft das beim Login korrekt.
--
-- ON CONFLICT DO NOTHING: falls jemand schon manuell einen User mit
-- gleichem Username/Email registriert hat, bricht die Migration nicht.

INSERT INTO users (username, email, password_hash, role, active) VALUES
    ('admin',  'admin@bugtracker.local',  '$2a$10$QYMClq1uXbiTxqhLld3wQe3Nj.aqC6V/cYwn9GR4UhLMY/kiq2wPy', 'ADMIN',     TRUE),
    ('dev',    'dev@bugtracker.local',    '$2a$10$QYMClq1uXbiTxqhLld3wQe3Nj.aqC6V/cYwn9GR4UhLMY/kiq2wPy', 'DEVELOPER', TRUE),
    ('tester', 'tester@bugtracker.local', '$2a$10$QYMClq1uXbiTxqhLld3wQe3Nj.aqC6V/cYwn9GR4UhLMY/kiq2wPy', 'TESTER',    TRUE)
ON CONFLICT (username) DO NOTHING;
