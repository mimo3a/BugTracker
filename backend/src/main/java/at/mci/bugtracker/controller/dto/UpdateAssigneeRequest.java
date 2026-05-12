package at.mci.bugtracker.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record UpdateAssigneeRequest(
        Long assigneeId
) {}
