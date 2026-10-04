package com.example.demo.student;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for PATCH /api/students/{id}/status
 * Example JSON: { "status": "GRADUATED" }
 */
public record StatusUpdateRequest(
        @NotNull(message = "Status is required") StudentStatus status) {
}
