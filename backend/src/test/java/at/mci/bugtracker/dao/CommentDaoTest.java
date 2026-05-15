package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:bugtracker_comments;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.flyway.enabled=false"
})
@Import(CommentDao.class)
class CommentDaoTest {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("DROP TABLE IF EXISTS comments");
        jdbc.execute("DROP TABLE IF EXISTS bugs");
        jdbc.execute("DROP TABLE IF EXISTS users");

        jdbc.execute("""
                CREATE TABLE users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE bugs (
                    id BIGSERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL
                )
                """);
        jdbc.execute("""
                CREATE TABLE comments (
                    id BIGSERIAL PRIMARY KEY,
                    bug_id BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
                    user_id BIGINT NOT NULL REFERENCES users(id),
                    content TEXT NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        jdbc.update("""
                INSERT INTO users (id, username, email, password_hash) VALUES
                (1, 'marie', 'marie@example.com', 'hash'),
                (2, 'tom', 'tom@example.com', 'hash')
                """);
        jdbc.update("INSERT INTO bugs (id, title) VALUES (42, 'Login broken'), (43, 'Other')");
    }

    @Test
    void insertReturnsCommentWithGeneratedIdAndResolvedUserName() {
        Comment saved = commentDao.insert(42L, 1L, "Kann ich reproduzieren.");

        assertThat(saved.id()).isNotNull();
        assertThat(saved.bugId()).isEqualTo(42L);
        assertThat(saved.userId()).isEqualTo(1L);
        assertThat(saved.userName()).isEqualTo("marie");
        assertThat(saved.content()).isEqualTo("Kann ich reproduzieren.");
        assertThat(saved.createdAt()).isNotNull();
    }

    @Test
    void findByBugIdReturnsOnlyMatchingBugInChronologicalOrder() {
        commentDao.insert(42L, 1L, "Erster");
        commentDao.insert(42L, 2L, "Zweiter");
        commentDao.insert(43L, 1L, "Anderer Bug");

        List<Comment> comments = commentDao.findByBugId(42L);

        assertThat(comments).extracting(Comment::content)
                .containsExactly("Erster", "Zweiter");
        assertThat(comments).extracting(Comment::userName)
                .containsExactly("marie", "tom");
    }

    @Test
    void findByBugIdReturnsEmptyListWhenNoComments() {
        assertThat(commentDao.findByBugId(42L)).isEmpty();
    }
}
