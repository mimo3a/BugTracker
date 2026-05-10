package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BugResponse(
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
    public BugResponse {
        tagIds = tagIds == null ? List.of() : List.copyOf(tagIds);
        tagNames = tagNames == null ? List.of() : List.copyOf(tagNames);
    }
}
