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
        "spring.datasource.url=jdbc:h2:mem:bugtracker_activities;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
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
                    title VARCHAR(255) NOT NULL
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
                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbc.update("""
                INSERT INTO users (id, username, email, password_hash) VALUES
                (1, 'marie', 'marie@example.com', 'hash'),
                (2, 'tom', 'tom@example.com', 'hash')
                """);
        jdbc.update("INSERT INTO bugs (id, title) VALUES (42, 'Login broken')");
    }

    @Test
    void insertReturnsActivityWithGeneratedIdAndUserName() {
        Activity saved = activityDao.insert(42L, 1L, "UPDATED", "status", "NEU", "IN_BEARBEITUNG");

        assertThat(saved.id()).isNotNull();
        assertThat(saved.bugId()).isEqualTo(42L);
        assertThat(saved.userId()).isEqualTo(1L);
        assertThat(saved.userName()).isEqualTo("marie");
        assertThat(saved.action()).isEqualTo("UPDATED");
        assertThat(saved.field()).isEqualTo("status");
        assertThat(saved.oldValue()).isEqualTo("NEU");
        assertThat(saved.newValue()).isEqualTo("IN_BEARBEITUNG");
        assertThat(saved.createdAt()).isNotNull();
    }

    @Test
    void insertAllowsNullFieldAndValues() {
        Activity saved = activityDao.insert(42L, 1L, "CREATED", null, null, "Login broken");

        assertThat(saved.action()).isEqualTo("CREATED");
        assertThat(saved.field()).isNull();
        assertThat(saved.oldValue()).isNull();
        assertThat(saved.newValue()).isEqualTo("Login broken");
    }

    @Test
    void findByBugIdReturnsDescendingByCreatedAtThenId() {
        activityDao.insert(42L, 1L, "CREATED", null, null, "Login broken");
        activityDao.insert(42L, 2L, "UPDATED", "status", "NEU", "IN_BEARBEITUNG");
        activityDao.insert(42L, 1L, "UPDATED", "priority", "MITTEL", "HOCH");

        List<Activity> activities = activityDao.findByBugId(42L);

        assertThat(activities).hasSize(3);
        assertThat(activities.get(0).field()).isEqualTo("priority");
        assertThat(activities.get(1).field()).isEqualTo("status");
        assertThat(activities.get(2).action()).isEqualTo("CREATED");
    }

    @Test
    void findByBugIdReturnsEmptyForBugWithoutActivities() {
        assertThat(activityDao.findByBugId(42L)).isEmpty();
    }

    @Test
    void findByBugIdScopesToSingleBug() {
        jdbc.update("INSERT INTO bugs (id, title) VALUES (43, 'Other bug')");
        activityDao.insert(42L, 1L, "CREATED", null, null, "Bug A");
        activityDao.insert(43L, 1L, "CREATED", null, null, "Bug B");

        List<Activity> bug42 = activityDao.findByBugId(42L);

        assertThat(bug42).hasSize(1);
        assertThat(bug42.get(0).newValue()).isEqualTo("Bug A");
    }
}
