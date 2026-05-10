package at.mci.bugtracker.dao;

import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BugDao {
    private static final String SELECT_WITH_JOINS = """
            SELECT b.id,
                   b.title,
                   b.description,
                   b.status,
                   b.priority,
                   b.reporter_id,
                   reporter.username AS reporter_name,
                   b.assignee_id,
                   assignee.username AS assignee_name,
                   b.archived,
                   b.created_at,
                   b.updated_at
              FROM bugs b
              JOIN users reporter ON reporter.id = b.reporter_id
         LEFT JOIN users assignee ON assignee.id = b.assignee_id
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public BugDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Bug> findAll(BugFilter filter, int page) {
        BugFilter effectiveFilter = filter == null ? BugFilter.empty() : filter;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String whereClause = buildWhereClause(effectiveFilter, parameters);

        parameters.addValue("limit", effectiveFilter.pageSize());
        parameters.addValue("offset", Math.max(page, 0) * effectiveFilter.pageSize());

        String sql = SELECT_WITH_JOINS
                + whereClause
                + " ORDER BY b.updated_at DESC, b.id DESC LIMIT :limit OFFSET :offset";

        List<Bug> bugs = jdbc.query(sql, parameters, new BugRowMapper());
        return enrichWithTags(bugs);
    }

    public long count(BugFilter filter) {
        BugFilter effectiveFilter = filter == null ? BugFilter.empty() : filter;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String whereClause = buildWhereClause(effectiveFilter, parameters);

        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM bugs b" + whereClause, parameters, Long.class);
        return total == null ? 0 : total;
    }

    public Optional<Bug> findById(Long id) {
        try {
            Bug bug = jdbc.queryForObject(
                    SELECT_WITH_JOINS + " WHERE b.id = :id",
                    new MapSqlParameterSource("id", id),
                    new BugRowMapper()
            );
            if (bug == null) {
                return Optional.empty();
            }
            return Optional.of(enrichWithTags(List.of(bug)).get(0));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Bug save(Bug bug) {
        BugStatus status = bug.status() == null ? BugStatus.NEU : bug.status();
        BugPriority priority = bug.priority() == null ? BugPriority.MITTEL : bug.priority();

        String sql = """
                INSERT INTO bugs (title, description, status, priority, reporter_id, assignee_id, archived)
                VALUES (:title, :description, :status, :priority, :reporterId, :assigneeId, :archived)
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("title", bug.title())
                .addValue("description", bug.description())
                .addValue("status", status.name())
                .addValue("priority", priority.name())
                .addValue("reporterId", bug.reporterId())
                .addValue("assigneeId", bug.assigneeId())
                .addValue("archived", bug.archived());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, parameters, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Bug insert did not return a generated id");
        }
        long bugId = key.longValue();

        insertBugTags(bugId, bug.tagIds());

        return findById(bugId).orElseThrow();
    }

    public Optional<Bug> update(Bug bug) {
        String sql = """
                UPDATE bugs
                   SET title = :title,
                       description = :description,
                       status = :status,
                       priority = :priority,
                       assignee_id = :assigneeId,
                       archived = :archived,
                       updated_at = CURRENT_TIMESTAMP
                 WHERE id = :id
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", bug.id())
                .addValue("title", bug.title())
                .addValue("description", bug.description())
                .addValue("status", bug.status().name())
                .addValue("priority", bug.priority().name())
                .addValue("assigneeId", bug.assigneeId())
                .addValue("archived", bug.archived());

        int updatedRows = jdbc.update(sql, parameters);
        if (updatedRows == 0) {
            return Optional.empty();
        }

        // Tag-Set komplett ersetzen: erst alle löschen, dann gewünschte einfügen.
        // Pragmatisch und korrekt für M:N — die bug_tags-Zeilen sind winzig, der
        // Overhead ist vernachlässigbar gegenüber der Komplexität von Diff-Logik.
        jdbc.update("DELETE FROM bug_tags WHERE bug_id = :bugId",
                new MapSqlParameterSource("bugId", bug.id()));
        insertBugTags(bug.id(), bug.tagIds());

        return findById(bug.id());
    }

    public Optional<Bug> updateStatus(Long id, BugStatus status) {
        String sql = """
                UPDATE bugs
                   SET status = :status,
                       archived = CASE WHEN :status = 'ARCHIVIERT' THEN TRUE ELSE archived END,
                       updated_at = CURRENT_TIMESTAMP
                 WHERE id = :id
                """;

        int updatedRows = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("status", status.name()));

        if (updatedRows == 0) {
            return Optional.empty();
        }

        return findById(id);
    }

    public boolean archive(Long id) {
        return setArchived(id, true);
    }

    public boolean restore(Long id) {
        return setArchived(id, false);
    }

    private boolean setArchived(Long id, boolean archived) {
        String sql = """
                UPDATE bugs
                   SET archived = :archived,
                       status = CASE WHEN :archived = TRUE THEN 'ARCHIVIERT' ELSE status END,
                       updated_at = CURRENT_TIMESTAMP
                 WHERE id = :id
                """;

        int updatedRows = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("archived", archived));

        return updatedRows > 0;
    }

    private void insertBugTags(Long bugId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<Long> distinctTagIds = tagIds.stream().distinct().toList();
        String sql = "INSERT INTO bug_tags (bug_id, tag_id) VALUES (:bugId, :tagId)";
        SqlParameterSource[] batch = distinctTagIds.stream()
                .map(tagId -> (SqlParameterSource) new MapSqlParameterSource()
                        .addValue("bugId", bugId)
                        .addValue("tagId", tagId))
                .toArray(SqlParameterSource[]::new);
        jdbc.batchUpdate(sql, batch);
    }

    private List<Bug> enrichWithTags(List<Bug> bugs) {
        if (bugs.isEmpty()) {
            return bugs;
        }
        List<Long> bugIds = bugs.stream().map(Bug::id).toList();
        Map<Long, List<TagRef>> tagsByBugId = fetchTagsByBugId(bugIds);

        List<Bug> enriched = new ArrayList<>(bugs.size());
        for (Bug b : bugs) {
            List<TagRef> refs = tagsByBugId.getOrDefault(b.id(), List.of());
            enriched.add(new Bug(
                    b.id(), b.title(), b.description(),
                    b.status(), b.priority(),
                    b.reporterId(), b.reporterName(),
                    b.assigneeId(), b.assigneeName(),
                    refs.stream().map(TagRef::id).toList(),
                    refs.stream().map(TagRef::name).toList(),
                    b.archived(),
                    b.createdAt(), b.updatedAt()
            ));
        }
        return enriched;
    }

    private Map<Long, List<TagRef>> fetchTagsByBugId(List<Long> bugIds) {
        String sql = """
                SELECT bt.bug_id, t.id AS tag_id, t.name AS tag_name
                  FROM bug_tags bt
                  JOIN tags t ON t.id = bt.tag_id
                 WHERE bt.bug_id IN (:bugIds)
                 ORDER BY bt.bug_id, t.id
                """;

        ResultSetExtractor<Map<Long, List<TagRef>>> extractor = rs -> {
            Map<Long, List<TagRef>> result = new LinkedHashMap<>();
            while (rs.next()) {
                long bugId = rs.getLong("bug_id");
                result.computeIfAbsent(bugId, k -> new ArrayList<>())
                        .add(new TagRef(rs.getLong("tag_id"), rs.getString("tag_name")));
            }
            return result;
        };

        Map<Long, List<TagRef>> map = jdbc.query(
                sql,
                new MapSqlParameterSource("bugIds", bugIds),
                extractor);
        return map == null ? new HashMap<>() : map;
    }

    private String buildWhereClause(BugFilter filter, MapSqlParameterSource parameters) {
        List<String> conditions = new ArrayList<>();

        if (!filter.includeArchived()) {
            conditions.add("b.archived = FALSE");
        }
        if (!filter.statuses().isEmpty()) {
            conditions.add("b.status IN (:statuses)");
            parameters.addValue("statuses", filter.statuses().stream().map(BugStatus::name).toList());
        }
        if (filter.priority() != null) {
            conditions.add("b.priority = :priority");
            parameters.addValue("priority", filter.priority().name());
        }
        if (filter.assigneeId() != null) {
            conditions.add("b.assignee_id = :assigneeId");
            parameters.addValue("assigneeId", filter.assigneeId());
        }
        if (filter.tagId() != null) {
            conditions.add("EXISTS (SELECT 1 FROM bug_tags bt WHERE bt.bug_id = b.id AND bt.tag_id = :tagId)");
            parameters.addValue("tagId", filter.tagId());
        }
        if (filter.search() != null && !filter.search().isBlank()) {
            conditions.add("LOWER(b.title) LIKE :search");
            parameters.addValue("search", "%" + filter.search().toLowerCase() + "%");
        }

        if (conditions.isEmpty()) {
            return "";
        }

        return " WHERE " + String.join(" AND ", conditions);
    }

    private record TagRef(Long id, String name) {}

    private static class BugRowMapper implements RowMapper<Bug> {
        @Override
        public Bug mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Bug(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    BugStatus.valueOf(rs.getString("status")),
                    BugPriority.valueOf(rs.getString("priority")),
                    rs.getLong("reporter_id"),
                    rs.getString("reporter_name"),
                    nullableLong(rs, "assignee_id"),
                    rs.getString("assignee_name"),
                    List.of(),
                    List.of(),
                    rs.getBoolean("archived"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getObject("updated_at", LocalDateTime.class)
            );
        }

        private static Long nullableLong(ResultSet rs, String column) throws SQLException {
            long value = rs.getLong(column);
            return rs.wasNull() ? null : value;
        }
    }
}
