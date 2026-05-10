package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.BugPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateBugRequest(
        @NotBlank(message = "Titel ist erforderlich")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Beschreibung ist erforderlich")
        String description,

        BugPriority priority,

        List<Long> tagIds
) {
    public CreateBugRequest {
        tagIds = tagIds == null ? List.of() : List.copyOf(tagIds);
    }
}
