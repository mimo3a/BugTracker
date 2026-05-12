package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:bugtracker;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.flyway.enabled=false"
})
@Import(ActivityDao.class)
class ActivityDaoTest {
    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("DROP TABLE IF EXISTS activities");
        jdbc.execute("DROP TABLE IF EXISTS bugs");
        jdbc.execute("DROP TABLE IF EXISTS users");

        jdbc.execute("""
                CREATE TABLE users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL DEFAULT 'TESTER',
                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        jdbc.execute("""
                CREATE TABLE bugs (
                    id BIGSERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    description TEXT NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'NEU',
                    priority VARCHAR(20) NOT NULL DEFAULT 'MITTEL',
                    reporter_id BIGINT NOT NULL REFERENCES users(id),
                    assignee_id BIGINT REFERENCES users(id),
                    archived BOOLEAN NOT NULL DEFAULT FALSE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        jdbc.execute("""
                CREATE TABLE activities (
                    id BIGSERIAL PRIMARY KEY,
                    bug_id BIGINT NOT NULL REFERENCES bugs(id),
                    user_id BIGINT NOT NULL REFERENCES users(id),
                    action VARCHAR(50) NOT NULL,
                    field VARCHAR(50),
                    old_value TEXT,
                    new_value TEXT,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbc.update("""
                INSERT INTO users (id, username, email, password_hash, role) VALUES
                (1, 'tom', 'tom@example.com', 'hash', 'TESTER'),
                (2, 'marie', 'marie@example.com', 'hash', 'DEVELOPER')
                """);
        jdbc.update("""
                INSERT INTO bugs (id, title, description, reporter_id) VALUES
                (10, 'Login fails', 'Steps', 1),
                (11, 'Other bug', 'Steps', 1)
                """);
    }

    @Test
    void findByBugIdReturnsNewestActivitiesFirstWithAllFields() {
        jdbc.update("""
                INSERT INTO activities (id, bug_id, user_id, action, field, old_value, new_value, created_at) VALUES
                (1, 10, 1, 'UPDATED', 'title', 'Old', 'New', TIMESTAMP '2026-05-10 10:00:00'),
                (2, 10, 2, 'UPDATED', 'status', 'NEU', 'IN_BEARBEITUNG', TIMESTAMP '2026-05-10 11:00:00'),
                (3, 11, 2, 'UPDATED', 'title', 'Other', 'Ignored', TIMESTAMP '2026-05-10 12:00:00')
                """);

        List<Activity> activities = activityDao.findByBugId(10L);

        assertThat(activities).hasSize(2);
        assertThat(activities).extracting(Activity::id).containsExactly(2L, 1L);

        Activity newest = activities.get(0);
        assertThat(newest.bugId()).isEqualTo(10L);
        assertThat(newest.userId()).isEqualTo(2L);
        assertThat(newest.userName()).isEqualTo("marie");
        assertThat(newest.action()).isEqualTo("UPDATED");
        assertThat(newest.field()).isEqualTo("status");
        assertThat(newest.oldValue()).isEqualTo("NEU");
        assertThat(newest.newValue()).isEqualTo("IN_BEARBEITUNG");
        assertThat(newest.createdAt()).isNotNull();
    }

    @Test
    void insertPersistsActivityForBug() {
        activityDao.insert(10L, 2L, "UPDATED", "description", "old", "new");

        List<Activity> activities = activityDao.findByBugId(10L);

        assertThat(activities).hasSize(1);
        Activity activity = activities.get(0);
        assertThat(activity.userName()).isEqualTo("marie");
        assertThat(activity.action()).isEqualTo("UPDATED");
        assertThat(activity.field()).isEqualTo("description");
        assertThat(activity.oldValue()).isEqualTo("old");
        assertThat(activity.newValue()).isEqualTo("new");
    }
}
