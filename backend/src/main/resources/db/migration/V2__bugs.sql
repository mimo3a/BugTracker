CREATE TABLE tags (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    color      VARCHAR(7),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bugs (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'NEU'
                CHECK (status IN ('NEU', 'IN_BEARBEITUNG', 'IM_REVIEW', 'ERLEDIGT', 'ABGELEHNT', 'ARCHIVIERT')),
    priority    VARCHAR(20) NOT NULL DEFAULT 'MITTEL'
                CHECK (priority IN ('NIEDRIG', 'MITTEL', 'HOCH', 'KRITISCH')),
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    assignee_id BIGINT REFERENCES users(id),
    archived    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bug_tags (
    bug_id BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE RESTRICT,
    PRIMARY KEY (bug_id, tag_id)
);

INSERT INTO tags (name, color) VALUES
    ('Backend',  '#3B82F6'),
    ('Frontend', '#10B981'),
    ('Bug',      '#EF4444'),
    ('Feature',  '#8B5CF6');
