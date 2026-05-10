package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.controller.dto.BugListResponse;
import at.mci.bugtracker.controller.dto.BugResponse;
import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.controller.dto.UpdateBugStatusRequest;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.service.BugService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) BugPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String search,
            @RequestParam(name = "archived", defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int pageSize
    ) {
        BugFilter filter = new BugFilter(statuses, priority, assigneeId, tagId, search, includeArchived, pageSize);
        List<BugResponse> bugs = bugService.findAll(filter, page).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(new BugListResponse(bugs, bugService.count(filter), page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BugResponse> getBug(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(bugService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BugResponse> createBug(@Valid @RequestBody CreateBugRequest request) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.createBug(request, session.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(bug));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DEVELOPER', 'ADMIN')")
    public ResponseEntity<BugResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBugStatusRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.updateStatus(id, request.status(), session.userId());
        return ResponseEntity.ok(toResponse(bug));
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
