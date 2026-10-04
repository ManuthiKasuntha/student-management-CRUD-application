package com.example.demo.student;

import java.util.List;

/**
 * Summary numbers returned by GET /api/students/stats
 */
public record StudentStats(
        long total,
        long active,
        long graduated,
        long suspended,
        double averageAge,
        List<CourseCount> byCourse) {
}
