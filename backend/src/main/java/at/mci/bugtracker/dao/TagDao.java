package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Tag;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.Optional;

@Repository
public class TagDao {

    private static final String SELECT_ALL = """
            SELECT id, name, color, created_at
              FROM tags
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public TagDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Tag> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY name ASC", new TagRowMapper());
    }

    public Optional<Tag> findById(long id) {
        try {
            Tag tag = jdbc.queryForObject(
                    SELECT_ALL + " WHERE id = :id",
                    new MapSqlParameterSource("id", id),
                    new TagRowMapper());
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Optional<Tag> findByName(String name) {
        try {
            Tag tag = jdbc.queryForObject(
                    SELECT_ALL + " WHERE LOWER(name) = LOWER(:name)",
                    new MapSqlParameterSource("name", name),
                    new TagRowMapper());
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Tag save(String name, String color) {
        String sql = "INSERT INTO tags (name, color) VALUES (:name, :color)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("color", color);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Tag insert did not return a generated id");
        }
        return findById(key.longValue()).orElseThrow();
    }

    public Optional<Tag> update(long id, String name, String color) {
        int rows = jdbc.update(
                "UPDATE tags SET name = :name, color = :color WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("name", name)
                        .addValue("color", color));
        if (rows == 0) {
            return Optional.empty();
        }
        return findById(id);
    }

    public boolean delete(long id) {
        // bug_tags.tag_id ist ON DELETE RESTRICT — die Junction-Zeilen müssen
        // VOR dem Tag-Delete weg, sonst wirft Postgres einen FK-Constraint-
        // Fehler. Die Bugs selbst bleiben unangetastet (kein Cascade) — sie
        // verlieren nur die Verknüpfung zu diesem Tag (FA-16 DoD).
        jdbc.update("DELETE FROM bug_tags WHERE tag_id = :id",
                new MapSqlParameterSource("id", id));
        int rows = jdbc.update("DELETE FROM tags WHERE id = :id",
                new MapSqlParameterSource("id", id));
        return rows > 0;
    }

    private static class TagRowMapper implements RowMapper<Tag> {
        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Tag(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color"),
                    rs.getObject("created_at", OffsetDateTime.class)
            );
        }
    }
}
