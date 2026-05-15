-- FA-11 Kommentarfunktion (T061). V4 ist bereits seed_demo_users, daher V5.
--
-- bug_id ON DELETE CASCADE: wird ein Bug gelöscht, verschwinden seine
-- Kommentare mit — konsistent zu bug_tags / activities.
-- user_id ohne CASCADE (RESTRICT default): ein User mit Kommentaren darf
-- nicht hart gelöscht werden; Deaktivierung läuft über users.active (FA-15).
CREATE TABLE comments (
    id         BIGSERIAL PRIMARY KEY,
    bug_id     BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    content    TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Kommentare werden pro Bug chronologisch (älteste zuerst) gelistet —
-- der Index deckt genau dieses Zugriffsmuster ab.
CREATE INDEX idx_comments_bug_created ON comments(bug_id, created_at);
