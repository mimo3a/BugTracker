package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class CommentDao {

    private static final String SELECT_WITH_USER = """
            SELECT c.id,
                   c.bug_id,
                   c.user_id,
                   u.username AS user_name,
                   c.content,
                   c.created_at
              FROM comments c
              JOIN users u ON u.id = c.user_id
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public CommentDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Comment> findByBugId(long bugId) {
        // Chronologisch aufsteigend (älteste zuerst) — deckt sich mit der
        // UI-Darstellung in CommentsSection und nutzt idx_comments_bug_created.
        return jdbc.query(
                SELECT_WITH_USER + " WHERE c.bug_id = :bugId ORDER BY c.created_at ASC, c.id ASC",
                new MapSqlParameterSource("bugId", bugId),
                new CommentRowMapper());
    }

    public Comment insert(long bugId, long userId, String content) {
        String sql = """
                INSERT INTO comments (bug_id, user_id, content)
                VALUES (:bugId, :userId, :content)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bugId", bugId)
                .addValue("userId", userId)
                .addValue("content", content);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Comment insert did not return a generated id");
        }
        return findById(key.longValue());
    }

    private Comment findById(long id) {
        return jdbc.queryForObject(
                SELECT_WITH_USER + " WHERE c.id = :id",
                new MapSqlParameterSource("id", id),
                new CommentRowMapper());
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Comment(
                    rs.getLong("id"),
                    rs.getLong("bug_id"),
                    rs.getLong("user_id"),
                    rs.getString("user_name"),
                    rs.getString("content"),
                    rs.getObject("created_at", OffsetDateTime.class)
            );
        }
    }
}
