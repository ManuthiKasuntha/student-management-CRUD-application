package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The JSON body sent back for EVERY error, so the client always gets the same shape:
 * {
 *   "timestamp": "2026-10-04T18:30:00",
 *   "status": 400,
 *   "message": "Validation failed",
 *   "errors": { "email": "Email must be a valid email address" }
 * }
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> errors) {

    public ErrorResponse(int status, String message) {
        this(LocalDateTime.now(), status, message, Map.of());
    }

    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this(LocalDateTime.now(), status, message, errors);
    }
}
