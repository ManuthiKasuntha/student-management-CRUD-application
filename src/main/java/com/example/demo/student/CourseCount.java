package com.example.demo.student;

/**
 * One row of "how many students are in each course", e.g. ("Software Engineering", 4).
 * Filled directly by a JPQL query in StudentRepository.
 */
public record CourseCount(String course, long count) {
}
