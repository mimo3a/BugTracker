package at.mci.bugtracker.controller.dto;

import at.mci.bugtracker.model.UserRole;

public record UpdateUserRequest(
        UserRole role,
        Boolean active
) {}
