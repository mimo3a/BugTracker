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

    // Activity-Actions (FA-14). Schema bewusst polymorph: 'action' nennt die
    // Operation, 'field' bei UPDATED welches Feld. Siehe V3__activities.sql.
    static final String ACTION_CREATED = "CREATED";
    static final String ACTION_UPDATED = "UPDATED";

    private final BugDao bugDao;
    private final UserDao userDao;
    private final ActivityDao activityDao;
    private final BugStatusStateMachine statusStateMachine;

    public BugService(BugDao bugDao, UserDao userDao, ActivityDao activityDao,
                      BugStatusStateMachine statusStateMachine) {
        this.bugDao = bugDao;
        this.userDao = userDao;
        this.activityDao = activityDao;
        this.statusStateMachine = statusStateMachine;
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
        activityDao.insert(saved.id(), reporterId, ACTION_CREATED, null, null, saved.title());
        return saved;
    }

    public Bug getBugById(Long id) {
        return bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));
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
        Bug existing = requireEditable(id);
        User actor = requireActor(userId);
        requireCanEditBugContent(existing, actor);

        List<Long> tagIds = request.tagIds() != null ? request.tagIds() : existing.tagIds();

        // Status / Priority / Assignee laufen über eigene PATCH-Endpoints und
        // werden hier bewusst nicht angefasst.
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

        recordChange(saved.id(), actor.id(), "title", existing.title(), saved.title());
        recordChange(saved.id(), actor.id(), "description", existing.description(), saved.description());
        recordChange(saved.id(), actor.id(), "tagIds", existing.tagIds(), saved.tagIds());

        return saved;
    }

    @Transactional
    public Bug updateStatus(Long id, BugStatus newStatus, long userId) {
        Bug existing = requireEditable(id);
        User actor = requireActor(userId);
        // FA-06: Status-Wechsel ist DEVELOPER+ADMIN-only. TESTER kann nicht.
        if (!isPrivileged(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, Status zu ändern");
        }
        if (!statusStateMachine.canTransition(existing.status(), newStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Status-Wechsel nicht erlaubt: " + existing.status() + " → " + newStatus);
        }

        Bug updated = new Bug(
                existing.id(),
                existing.title(),
                existing.description(),
                newStatus,
                existing.priority(),
                existing.reporterId(),
                existing.reporterName(),
                existing.assigneeId(),
                existing.assigneeName(),
                existing.tagIds(),
                existing.tagNames(),
                existing.archived(),
                existing.createdAt(),
                existing.updatedAt()
        );
        Bug saved = bugDao.update(updated)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));

        recordChange(saved.id(), actor.id(), "status", existing.status(), saved.status());
        return saved;
    }

    @Transactional
    public Bug updateAssignee(Long id, Long newAssigneeId, long userId) {
        Bug existing = requireEditable(id);
        User actor = requireActor(userId);
        if (!isPrivileged(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, Bearbeiter zu ändern");
        }

        String newAssigneeName = null;
        if (newAssigneeId != null) {
            User assignee = userDao.findById(newAssigneeId);
            if (assignee == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden");
            }
            newAssigneeName = assignee.username();
        }

        Bug updated = new Bug(
                existing.id(),
                existing.title(),
                existing.description(),
                existing.status(),
                existing.priority(),
                existing.reporterId(),
                existing.reporterName(),
                newAssigneeId,
                newAssigneeName,
                existing.tagIds(),
                existing.tagNames(),
                existing.archived(),
                existing.createdAt(),
                existing.updatedAt()
        );
        Bug saved = bugDao.update(updated)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));

        recordChange(saved.id(), actor.id(), "assigneeId", existing.assigneeId(), saved.assigneeId());
        return saved;
    }

    @Transactional
    public Bug archiveBug(Long id, long userId) {
        Bug existing = bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));
        if (existing.archived()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bug ist bereits archiviert");
        }
        User actor = requireActor(userId);
        if (!isPrivileged(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, Bug zu archivieren");
        }
        bugDao.archive(id);
        Bug saved = bugDao.findById(id).orElseThrow();
        activityDao.insert(saved.id(), userId, ACTION_UPDATED, "archived", "false", "true");
        return saved;
    }

    @Transactional
    public Bug restoreBug(Long id, long userId) {
        Bug existing = bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));
        if (!existing.archived()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bug ist nicht archiviert");
        }
        User actor = requireActor(userId);
        if (!isPrivileged(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, Bug zu reaktivieren");
        }
        bugDao.restore(id);
        Bug saved = bugDao.findById(id).orElseThrow();
        activityDao.insert(saved.id(), userId, ACTION_UPDATED, "archived", "true", "false");
        return saved;
    }

    // --- Helper -----------------------------------------------------------

    private Bug requireEditable(Long id) {
        Bug existing = bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));
        if (existing.archived()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Archivierter Bug kann nicht bearbeitet werden");
        }
        return existing;
    }

    private User requireActor(long userId) {
        User actor = userDao.findById(userId);
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session ungültig");
        }
        return actor;
    }

    private void requireCanEditBugContent(Bug existing, User actor) {
        // FA-04: DEVELOPER + ADMIN dürfen jeden Bug editieren; TESTER nur den eigenen.
        boolean ownReport = actor.role() == UserRole.TESTER && existing.reporterId() == actor.id();
        if (!isPrivileged(actor) && !ownReport) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Keine Berechtigung, diesen Bug zu bearbeiten");
        }
    }

    private boolean isPrivileged(User actor) {
        return actor.role() == UserRole.ADMIN || actor.role() == UserRole.DEVELOPER;
    }

    private void recordChange(long bugId, long userId, String field, Object oldValue, Object newValue) {
        if (Objects.equals(oldValue, newValue)) return;
        activityDao.insert(bugId, userId, ACTION_UPDATED, field, stringOf(oldValue), stringOf(newValue));
    }

    private static String stringOf(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
