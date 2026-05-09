package at.mci.bugtracker.model;

import java.time.LocalDateTime;

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
        Long tagId,
        String tagName,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
