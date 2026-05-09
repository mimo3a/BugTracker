package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:bugtracker;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.flyway.enabled=false"
})
@Import(BugDao.class)
class BugDaoTest {
    @Autowired
    private BugDao bugDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("DROP TABLE IF EXISTS bugs");
        jdbc.execute("DROP TABLE IF EXISTS tags");
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
                CREATE TABLE tags (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(50) UNIQUE NOT NULL,
                    color VARCHAR(7),
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
                    tag_id BIGINT REFERENCES tags(id),
                    archived BOOLEAN NOT NULL DEFAULT FALSE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbc.update("""
                INSERT INTO users (id, username, email, password_hash, role) VALUES
                (1, 'tom', 'tom@example.com', 'hash', 'TESTER'),
                (2, 'marie', 'marie@example.com', 'hash', 'DEVELOPER'),
                (3, 'sandra', 'sandra@example.com', 'hash', 'ADMIN')
                """);
        jdbc.update("""
                INSERT INTO tags (id, name, color) VALUES
                (1, 'Backend', '#3B82F6'),
                (2, 'Frontend', '#10B981')
                """);
    }

    @Test
    void saveCreatesBugWithDefaultsAndJoinFields() {
        Bug saved = bugDao.save(newBug("Login button broken", null, null, null, 1L));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.status()).isEqualTo(BugStatus.NEU);
        assertThat(saved.priority()).isEqualTo(BugPriority.MITTEL);
        assertThat(saved.reporterName()).isEqualTo("tom");
        assertThat(saved.tagName()).isEqualTo("Backend");
        assertThat(saved.createdAt()).isNotNull();
        assertThat(saved.updatedAt()).isNotNull();
    }

    @Test
    void findByIdReturnsBugWithReporterAssigneeAndTag() {
        Bug saved = bugDao.save(newBug("API fails", BugStatus.IN_BEARBEITUNG, BugPriority.HOCH, 2L, 1L));

        Optional<Bug> found = bugDao.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().reporterName()).isEqualTo("tom");
        assertThat(found.orElseThrow().assigneeName()).isEqualTo("marie");
        assertThat(found.orElseThrow().tagName()).isEqualTo("Backend");
    }

    @Test
    void findByIdReturnsEmptyForMissingBug() {
        assertThat(bugDao.findById(999L)).isEmpty();
    }

    @Test
    void updateChangesMutableFields() {
        Bug saved = bugDao.save(newBug("Old title", BugStatus.NEU, BugPriority.MITTEL, null, 1L));
        Bug changed = new Bug(
                saved.id(),
                "New title",
                "Updated description",
                BugStatus.IN_BEARBEITUNG,
                BugPriority.KRITISCH,
                saved.reporterId(),
                saved.reporterName(),
                3L,
                null,
                2L,
                null,
                false,
                saved.createdAt(),
                saved.updatedAt()
        );

        Optional<Bug> updated = bugDao.update(changed);

        assertThat(updated).isPresent();
        Bug bug = updated.orElseThrow();
        assertThat(bug.title()).isEqualTo("New title");
        assertThat(bug.description()).isEqualTo("Updated description");
        assertThat(bug.status()).isEqualTo(BugStatus.IN_BEARBEITUNG);
        assertThat(bug.priority()).isEqualTo(BugPriority.KRITISCH);
        assertThat(bug.assigneeName()).isEqualTo("sandra");
        assertThat(bug.tagName()).isEqualTo("Frontend");
    }

    @Test
    void updateReturnsEmptyForMissingBug() {
        Bug missing = new Bug(999L, "Missing", "Missing", BugStatus.NEU, BugPriority.MITTEL,
                1L, null, null, null, null, null, false, null, null);

        assertThat(bugDao.update(missing)).isEmpty();
    }

    @Test
    void archiveAndRestoreToggleArchivedFlag() {
        Bug saved = bugDao.save(newBug("Archive me", BugStatus.NEU, BugPriority.MITTEL, null, null));

        assertThat(bugDao.archive(saved.id())).isTrue();
        Bug archived = bugDao.findById(saved.id()).orElseThrow();
        assertThat(archived.archived()).isTrue();
        assertThat(archived.status()).isEqualTo(BugStatus.ARCHIVIERT);

        assertThat(bugDao.restore(saved.id())).isTrue();
        Bug restored = bugDao.findById(saved.id()).orElseThrow();
        assertThat(restored.archived()).isFalse();
        assertThat(restored.status()).isEqualTo(BugStatus.ARCHIVIERT);
    }

    @Test
    void archiveReturnsFalseForMissingBug() {
        assertThat(bugDao.archive(999L)).isFalse();
        assertThat(bugDao.restore(999L)).isFalse();
    }

    @Test
    void findAllAppliesStatusPriorityAssigneeTagSearchAndArchivedFilters() {
        bugDao.save(newBug("Login button broken", BugStatus.NEU, BugPriority.HOCH, 2L, 1L));
        bugDao.save(newBug("Registration layout broken", BugStatus.NEU, BugPriority.HOCH, 3L, 2L));
        bugDao.save(newBug("Login copy typo", BugStatus.ERLEDIGT, BugPriority.NIEDRIG, 2L, 1L));
        Bug archived = bugDao.save(newBug("Login archived", BugStatus.NEU, BugPriority.HOCH, 2L, 1L));
        bugDao.archive(archived.id());

        BugFilter filter = new BugFilter(
                List.of(BugStatus.NEU),
                BugPriority.HOCH,
                2L,
                1L,
                "login",
                false,
                50
        );

        List<Bug> bugs = bugDao.findAll(filter, 0);

        assertThat(bugs)
                .extracting(Bug::title)
                .containsExactly("Login button broken");
        assertThat(bugDao.count(filter)).isEqualTo(1);
    }

    @Test
    void findAllCanIncludeArchivedBugs() {
        Bug active = bugDao.save(newBug("Active bug", BugStatus.NEU, BugPriority.MITTEL, null, null));
        Bug archived = bugDao.save(newBug("Archived bug", BugStatus.NEU, BugPriority.MITTEL, null, null));
        bugDao.archive(archived.id());

        List<Bug> defaultResult = bugDao.findAll(BugFilter.empty(), 0);
        BugFilter includeArchived = new BugFilter(List.of(), null, null, null, null, true, 50);

        assertThat(defaultResult).extracting(Bug::id).containsExactly(active.id());
        assertThat(bugDao.findAll(includeArchived, 0)).extracting(Bug::id).contains(archived.id(), active.id());
    }

    @Test
    void findAllPaginatesResults() {
        Bug first = bugDao.save(newBug("First", BugStatus.NEU, BugPriority.MITTEL, null, null));
        Bug second = bugDao.save(newBug("Second", BugStatus.NEU, BugPriority.MITTEL, null, null));
        Bug third = bugDao.save(newBug("Third", BugStatus.NEU, BugPriority.MITTEL, null, null));

        BugFilter filter = new BugFilter(List.of(), null, null, null, null, false, 2);

        assertThat(bugDao.findAll(filter, 0)).extracting(Bug::id).containsExactly(third.id(), second.id());
        assertThat(bugDao.findAll(filter, 1)).extracting(Bug::id).containsExactly(first.id());
    }

    private static Bug newBug(String title, BugStatus status, BugPriority priority, Long assigneeId, Long tagId) {
        return new Bug(
                null,
                title,
                "Steps to reproduce",
                status,
                priority,
                1L,
                null,
                assigneeId,
                null,
                tagId,
                null,
                false,
                null,
                null
        );
    }
}
