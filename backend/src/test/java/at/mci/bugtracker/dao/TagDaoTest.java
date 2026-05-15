package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Tag;
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
        "spring.datasource.url=jdbc:h2:mem:bugtracker_tags;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.flyway.enabled=false"
})
@Import(TagDao.class)
class TagDaoTest {
    @Autowired
    private TagDao tagDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("DROP TABLE IF EXISTS bug_tags");
        jdbc.execute("DROP TABLE IF EXISTS bugs");
        jdbc.execute("DROP TABLE IF EXISTS tags");

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
                    title VARCHAR(255) NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE bug_tags (
                    bug_id BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
                    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE RESTRICT,
                    PRIMARY KEY (bug_id, tag_id)
                )
                """);
        jdbc.update("""
                INSERT INTO tags (id, name, color) VALUES
                    (1, 'Backend', '#3B82F6'),
                    (2, 'Frontend', '#10B981')
                """);
        jdbc.update("INSERT INTO bugs (id, title) VALUES (42, 'Login broken')");
        jdbc.update("INSERT INTO bug_tags (bug_id, tag_id) VALUES (42, 1)");
        // Seed-Tags haben explizite ids 1/2; IDENTITY-Generator dahinter setzen,
        // sonst kollidiert der erste save()-INSERT mit PK 1.
        jdbc.execute("ALTER TABLE tags ALTER COLUMN id RESTART WITH 100");
    }

    @Test
    void findAllReturnsTagsSortedByName() {
        List<Tag> tags = tagDao.findAll();

        assertThat(tags).extracting(Tag::name).containsExactly("Backend", "Frontend");
        assertThat(tags.get(0).color()).isEqualTo("#3B82F6");
        assertThat(tags.get(0).createdAt()).isNotNull();
    }

    @Test
    void saveCreatesTagWithGeneratedId() {
        Tag saved = tagDao.save("Security", "#EF4444");

        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("Security");
        assertThat(saved.color()).isEqualTo("#EF4444");
        assertThat(tagDao.findById(saved.id())).isPresent();
    }

    @Test
    void saveAllowsNullColor() {
        Tag saved = tagDao.save("NoColor", null);

        assertThat(saved.color()).isNull();
    }

    @Test
    void findByNameIsCaseInsensitive() {
        assertThat(tagDao.findByName("backend")).isPresent();
        assertThat(tagDao.findByName("BACKEND")).map(Tag::id).contains(1L);
        assertThat(tagDao.findByName("does-not-exist")).isEmpty();
    }

    @Test
    void updateChangesNameAndColor() {
        Optional<Tag> updated = tagDao.update(2L, "UI", "#000000");

        assertThat(updated).isPresent();
        assertThat(updated.get().name()).isEqualTo("UI");
        assertThat(updated.get().color()).isEqualTo("#000000");
    }

    @Test
    void updateReturnsEmptyForUnknownId() {
        assertThat(tagDao.update(999L, "Ghost", "#FFFFFF")).isEmpty();
    }

    @Test
    void deleteRemovesTagAndItsBugTagLinksButKeepsBug() {
        boolean deleted = tagDao.delete(1L);

        assertThat(deleted).isTrue();
        assertThat(tagDao.findById(1L)).isEmpty();
        // Junction-Zeile ist weg ...
        Integer links = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bug_tags WHERE tag_id = 1", Integer.class);
        assertThat(links).isZero();
        // ... aber der Bug selbst bleibt erhalten (kein Cascade auf bugs).
        Integer bugs = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bugs WHERE id = 42", Integer.class);
        assertThat(bugs).isEqualTo(1);
    }

    @Test
    void deleteReturnsFalseForUnknownId() {
        assertThat(tagDao.delete(999L)).isFalse();
    }
}
