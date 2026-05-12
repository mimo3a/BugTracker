package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.controller.dto.UpdateBugRequest;
import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class BugService {

    private final BugDao bugDao;
    private final UserDao userDao;
    private final ActivityDao activityDao;

    public BugService(BugDao bugDao, UserDao userDao, ActivityDao activityDao) {
        this.bugDao = bugDao;
        this.userDao = userDao;
        this.activityDao = activityDao;
    }

    @Transactional
    public Bug createBug(CreateBugRequest request, long reporterId) {
        BugPriority priority = request.priority() != null ? request.priority() : BugPriority.MITTEL;

        Bug bug = new Bug(
                null,
                request.title(),
                request.description(),
                BugStatus.NEU,
                priority,
                reporterId,
                null,
                null,
                null,
                request.tagIds(),
                List.of(),
                false,
                null,
                null
        );
        Bug saved = bugDao.save(bug);
        activityDao.insert(saved.id(), reporterId, "CREATED", null, null, saved.title());
        return saved;
    }

    public BugPage listBugs(BugFilter filter, int page) {
        BugFilter effectiveFilter = filter == null ? BugFilter.empty() : filter;
        int effectivePage = Math.max(page, 0);

        List<Bug> bugs = bugDao.findAll(effectiveFilter, effectivePage);
        long total = bugDao.count(effectiveFilter);

        return new BugPage(bugs, total, effectivePage, effectiveFilter.pageSize());
    }

    @Transactional
    public Bug updateBug(Long id, UpdateBugRequest request, long userId) {
        Bug existing = bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));

        if (existing.archived()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Archivierter Bug kann nicht bearbeitet werden");
        }

        // FA-04: DEVELOPER + ADMIN dürfen jeden Bug editieren; TESTER (Reporter-Rolle)
        // nur den eigenen Bug. Spec: docs/api/openapi.yaml PUT /api/bugs/{id}
        User actor = userDao.findById(userId);
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session ungültig");
        }
        boolean privileged = actor.role() == UserRole.ADMIN || actor.role() == UserRole.DEVELOPER;
        boolean ownReport = actor.role() == UserRole.TESTER && existing.reporterId() == userId;
        if (!privileged && !ownReport) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, diesen Bug zu bearbeiten");
        }

        List<Long> tagIds = request.tagIds() != null ? request.tagIds() : existing.tagIds();

        // Priority bleibt unverändert — sie wird über PATCH /api/bugs/{id}/priority gesetzt.
        Bug updated = new Bug(
                existing.id(),
                request.title(),
                request.description(),
                existing.status(),
                existing.priority(),
                existing.reporterId(),
                existing.reporterName(),
                existing.assigneeId(),
                existing.assigneeName(),
                tagIds,
                existing.tagNames(),
                existing.archived(),
                existing.createdAt(),
                existing.updatedAt()
        );
        Bug saved = bugDao.update(updated)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));
        logChangedFields(existing, saved, userId);
        return saved;
    }

    private void logChangedFields(Bug before, Bug after, long userId) {
        logIfChanged(after.id(), userId, "UPDATED", "title", before.title(), after.title());
        logIfChanged(after.id(), userId, "UPDATED", "description", before.description(), after.description());

        if (!Objects.equals(before.tagIds(), after.tagIds())) {
            activityDao.insert(
                    after.id(),
                    userId,
                    "UPDATED",
                    "tags",
                    String.join(", ", before.tagNames()),
                    String.join(", ", after.tagNames())
            );
        }
    }

    private void logIfChanged(
            Long bugId,
            long userId,
            String action,
            String field,
            String oldValue,
            String newValue
    ) {
        if (!Objects.equals(oldValue, newValue)) {
            activityDao.insert(bugId, userId, action, field, oldValue, newValue);
        }
    }

    public record BugPage(List<Bug> bugs, long total, int page, int pageSize) {
        public BugPage {
            bugs = bugs == null ? List.of() : List.copyOf(bugs);
        }
    }
}
