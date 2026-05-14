package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.BugPriority;
import jakarta.validation.constraints.NotNull;

public record UpdatePriorityRequest(
        @NotNull(message = "Priorität ist erforderlich")
        BugPriority priority
) {}
