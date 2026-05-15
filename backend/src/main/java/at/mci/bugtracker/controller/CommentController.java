package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.controller.dto.CommentResponse;
import at.mci.bugtracker.controller.dto.CreateCommentRequest;
import at.mci.bugtracker.model.Comment;
import at.mci.bugtracker.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// FA-11 Kommentare (T061). Kein @PreAuthorize: jeder eingeloggte User
// (TESTER inkl.) darf Kommentare lesen und schreiben — laut Rollen-Matrix
// (T021b) ist FA-11 bereits auf TESTER-Ebene erlaubt. Routing analog
// ActivityController (nested unter /api/bugs/{bugId}).
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/bugs/{bugId}/comments")
    public List<CommentResponse> listComments(@PathVariable long bugId) {
        CurrentSession.require();
        return commentService.getComments(bugId).stream()
                .map(CommentController::toResponse)
                .toList();
    }

    @PostMapping("/api/bugs/{bugId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @PathVariable long bugId,
            @Valid @RequestBody CreateCommentRequest request) {
        SessionStore.Session session = CurrentSession.require();
        Comment comment = commentService.addComment(bugId, session.userId(), request.content());
        return toResponse(comment);
    }

    private static CommentResponse toResponse(Comment c) {
        return new CommentResponse(
                c.id(),
                c.bugId(),
                c.userId(),
                c.userName(),
                c.content(),
                c.createdAt()
        );
    }
}
