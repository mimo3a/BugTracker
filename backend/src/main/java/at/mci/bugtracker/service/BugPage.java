package at.mci.bugtracker.service;

import at.mci.bugtracker.model.Bug;

import java.util.List;

public record BugPage(List<Bug> bugs, long total, int page, int pageSize) {
    public BugPage {
        bugs = bugs == null ? List.of() : List.copyOf(bugs);
    }
}
