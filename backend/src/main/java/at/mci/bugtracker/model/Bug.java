package at.mci.bugtracker.model;

import java.time.LocalDateTime;
import java.util.List;

public record Bug(
        Long id,
        String title,
        String description,
        BugStatus status,
        BugPriority priority,
        Long reporterId,
        String reporterName,
        Long assigneeId,
        String assigneeName,
        List<Long> tagIds,
        List<String> tagNames,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Bug {
        tagIds = tagIds == null ? List.of() : List.copyOf(tagIds);
        tagNames = tagNames == null ? List.of() : List.copyOf(tagNames);
    }
}
