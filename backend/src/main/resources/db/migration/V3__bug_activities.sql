CREATE TABLE bug_activities (
    id         BIGSERIAL PRIMARY KEY,
    bug_id     BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    action     VARCHAR(50) NOT NULL,
    field      VARCHAR(50),
    old_value  TEXT,
    new_value  TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bug_activities_bug_created
    ON bug_activities (bug_id, created_at DESC);
