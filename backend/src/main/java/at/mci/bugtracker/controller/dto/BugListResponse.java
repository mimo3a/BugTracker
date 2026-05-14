package at.mci.bugtracker.controller.dto;

import java.util.List;

public record BugListResponse(
        List<BugResponse> bugs,
        long total,
        int page,
        int pageSize
) {
    public BugListResponse {
        bugs = bugs == null ? List.of() : List.copyOf(bugs);
    }
}
