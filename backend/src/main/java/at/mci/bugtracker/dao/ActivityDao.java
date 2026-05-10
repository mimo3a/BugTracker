package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Activity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ActivityDao {
    private final NamedParameterJdbcTemplate jdbc;

    public ActivityDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insertStatusChanged(Long bugId, Long userId, String oldValue, String newValue) {
        String sql = """
                INSERT INTO bug_activities (bug_id, user_id, action, field, old_value, new_value)
                VALUES (:bugId, :userId, 'STATUS_CHANGED', 'status', :oldValue, :newValue)
                """;

        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("bugId", bugId)
                .addValue("userId", userId)
                .addValue("oldValue", oldValue)
                .addValue("newValue", newValue));
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
                  FROM bug_activities a
                  JOIN users u ON u.id = a.user_id
                 WHERE a.bug_id = :bugId
                 ORDER BY a.created_at DESC, a.id DESC
                """;

        return jdbc.query(sql, new MapSqlParameterSource("bugId", bugId), new ActivityRowMapper());
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
                    rs.getObject("created_at", LocalDateTime.class)
            );
        }
    }
}
