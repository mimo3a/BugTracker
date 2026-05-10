package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.controller.dto.BugResponse;
import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.service.BugService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    private final BugService bugService;

    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BugResponse createBug(@Valid @RequestBody CreateBugRequest request) {
        SessionStore.Session session = CurrentSession.require();
        Bug bug = bugService.createBug(request, session.userId());
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
