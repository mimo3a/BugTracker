package at.mci.bugtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.controller.dto.BugListResponse;
import at.mci.bugtracker.controller.dto.BugResponse;
import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.controller.dto.UpdateAssigneeRequest;
import at.mci.bugtracker.controller.dto.UpdateBugRequest;
import at.mci.bugtracker.controller.dto.UpdatePriorityRequest;
import at.mci.bugtracker.controller.dto.UpdateStatusRequest;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.service.BugPage;
import at.mci.bugtracker.service.BugService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    private final BugService bugService;

    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @GetMapping
    public ResponseEntity<BugListResponse> listBugs(
            @RequestParam(name = "status", required = false) List<BugStatus> statuses,
            @RequestParam(name = "priority", required = false) BugPriority priority,
            @RequestParam(name = "assigneeId", required = false) Long assigneeId,
            @RequestParam(name = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "archived", defaultValue = "false") boolean archived,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        // pageSize ist bewusst nicht client-konfigurierbar — fester Wert verhindert
        // großflächige Queries und vereinfacht die API für die MVP-Phase.
        BugFilter filter = new BugFilter(
                statuses,
                priority,
                assigneeId,
                tagIds,
                search,
                archived,
                BugFilter.DEFAULT_PAGE_SIZE
        );
        BugPage bugPage = bugService.listBugs(filter, Math.max(page, 0));

        BugListResponse response = new BugListResponse(
                bugPage.bugs().stream().map(this::toResponse).toList(),
                bugPage.total(),
                bugPage.page(),
                bugPage.pageSize()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BugResponse createBug(@Valid @RequestBody CreateBugRequest request) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.createBug(request, session.userId());
        return toResponse(bug);
    }

    @PutMapping("/{id}")
    public BugResponse updateBug(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBugRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.updateBug(id, request, session.userId());
        return toResponse(bug);
    }

    @PatchMapping("/{id}/status")
    public BugResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.updateStatus(id, request.status(), session.userId());
        return toResponse(bug);
    }

    @PatchMapping("/{id}/priority")
    public BugResponse updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePriorityRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.updatePriority(id, request.priority(), session.userId());
        return toResponse(bug);
    }

    @PatchMapping("/{id}/assignee")
    public BugResponse updateAssignee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssigneeRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.updateAssignee(id, request.assigneeId(), session.userId());
        return toResponse(bug);
    }

    private BugResponse toResponse(Bug bug) {
        return new BugResponse(
                bug.id(),
                bug.title(),
                bug.description(),
                bug.status(),
                bug.priority(),
                bug.reporterId(),
                bug.reporterName(),
                bug.assigneeId(),
                bug.assigneeName(),
                bug.tagIds(),
                bug.tagNames(),
                bug.archived(),
                bug.createdAt(),
                bug.updatedAt()
        );
    }
}
