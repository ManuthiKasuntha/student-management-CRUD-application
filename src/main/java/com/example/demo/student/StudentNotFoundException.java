package com.example.demo.student;

/**
 * Thrown when a student id doesn't exist.
 * GlobalExceptionHandler turns it into HTTP 404 Not Found.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student not found with id " + id);
    }
}
