package at.mci.bugtracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateBugRequest(
        @NotBlank(message = "Titel ist erforderlich")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Beschreibung ist erforderlich")
        @Size(max = 10_000)
        String description,

        List<Long> tagIds
) {}
