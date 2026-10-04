package com.example.demo.student;

/**
 * Thrown when another student already uses the email.
 * GlobalExceptionHandler turns it into HTTP 409 Conflict.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already in use: " + email);
    }
}
