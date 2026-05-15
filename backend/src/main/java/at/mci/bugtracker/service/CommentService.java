package at.mci.bugtracker.service;

import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.dao.CommentDao;
import at.mci.bugtracker.model.Comment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommentService {

    private final CommentDao commentDao;
    private final BugDao bugDao;

    public CommentService(CommentDao commentDao, BugDao bugDao) {
        this.commentDao = commentDao;
        this.bugDao = bugDao;
    }

    public List<Comment> getComments(long bugId) {
        requireBugExists(bugId);
        return commentDao.findByBugId(bugId);
    }

    @Transactional
    public Comment addComment(long bugId, long userId, String content) {
        requireBugExists(bugId);
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kommentar darf nicht leer sein");
        }
        return commentDao.insert(bugId, userId, content.trim());
    }

    private void requireBugExists(long bugId) {
        // Existenz-Check wie in ActivityService — sonst wäre ein nicht
        // existierender Bug nicht von "hat noch keine Kommentare" unterscheidbar.
        if (bugDao.findById(bugId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden");
        }
    }
}
