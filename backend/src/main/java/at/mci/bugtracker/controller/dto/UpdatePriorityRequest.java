package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.BugPriority;
import jakarta.validation.constraints.NotNull;

public record UpdatePriorityRequest(
        @NotNull
        BugPriority priority
) {}
