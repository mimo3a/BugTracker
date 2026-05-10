package at.mci.bugtracker.model;

import java.util.List;

public record BugFilter(
        List<BugStatus> statuses,
        BugPriority priority,
        Long assigneeId,
        Long tagId,
        String search,
        boolean includeArchived,
        int pageSize
) {
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_PAGE_SIZE = 200;

    public BugFilter {
        statuses = statuses == null ? List.of() : List.copyOf(statuses);
        search = search == null ? null : search.trim();

        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
    }

    public static BugFilter empty() {
        return new BugFilter(List.of(), null, null, null, null, false, DEFAULT_PAGE_SIZE);
    }
}
