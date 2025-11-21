package com.ortecfinance.tasklist.api.rest.project.dto;

public record CreateTaskRequest(
        String projectName,
        String taskDescription
) {
}
