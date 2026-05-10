package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.BugStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateBugStatusRequest(
        @NotNull(message = "Status ist erforderlich")
        BugStatus status
) {}
