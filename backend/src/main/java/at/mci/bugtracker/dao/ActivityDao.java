package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Activity;
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
public class ActivityDao {
    private final NamedParameterJdbcTemplate jdbc;

    public ActivityDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Activity save(Activity activity) {
        String sql = """
                INSERT INTO activities (bug_id, user_id, action, field, old_value, new_value)
                VALUES (:bugId, :userId, :action, :field, :oldValue, :newValue)
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("bugId", activity.bugId())
                .addValue("userId", activity.userId())
                .addValue("action", activity.action())
                .addValue("field", activity.field())
                .addValue("oldValue", activity.oldValue())
                .addValue("newValue", activity.newValue());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, parameters, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Activity insert did not return a generated id");
        }
        return findById(key.longValue());
    }

    public List<Activity> findByBugId(Long bugId) {
        String sql = """
                SELECT a.id,
                       a.bug_id,
                       a.user_id,
                       u.username AS user_name,
                       a.action,
                       a.field,
                       a.old_value,
                       a.new_value,
                       a.created_at
                  FROM activities a
                  JOIN users u ON u.id = a.user_id
                 WHERE a.bug_id = :bugId
                 ORDER BY a.created_at DESC, a.id DESC
                """;

        return jdbc.query(sql, new MapSqlParameterSource("bugId", bugId), new ActivityRowMapper());
    }

    private Activity findById(Long id) {
        String sql = """
                SELECT a.id,
                       a.bug_id,
                       a.user_id,
                       u.username AS user_name,
                       a.action,
                       a.field,
                       a.old_value,
                       a.new_value,
                       a.created_at
                  FROM activities a
                  JOIN users u ON u.id = a.user_id
                 WHERE a.id = :id
                """;

        return jdbc.queryForObject(sql, new MapSqlParameterSource("id", id), new ActivityRowMapper());
    }

    private static class ActivityRowMapper implements RowMapper<Activity> {
        @Override
        public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Activity(
                    rs.getLong("id"),
                    rs.getLong("bug_id"),
                    rs.getLong("user_id"),
                    rs.getString("user_name"),
                    rs.getString("action"),
                    rs.getString("field"),
                    rs.getString("old_value"),
                    rs.getString("new_value"),
                    rs.getObject("created_at", OffsetDateTime.class)
            );
        }
    }
}
