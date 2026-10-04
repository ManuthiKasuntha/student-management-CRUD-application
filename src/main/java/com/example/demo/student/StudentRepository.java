package com.example.demo.student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * LAYER 2 — REPOSITORY (talks to the database)
 * By extending JpaRepository<Student, Long> you get these methods for FREE,
 * with no implementation code at all:
 *   save(), findById(), findAll(), deleteById(), existsById(), count() ...
 * Spring generates the implementation at startup.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ---- Derived queries: Spring builds the SQL from the method NAME ----

    // SELECT COUNT(*) > 0 FROM students WHERE email = ?
    boolean existsByEmail(String email);

    // Same, but ignore one student (used on update, so a student can keep their own email)
    // SELECT COUNT(*) > 0 FROM students WHERE email = ? AND id <> ?
    boolean existsByEmailAndIdNot(String email, Long id);

    // SELECT COUNT(*) FROM students WHERE status = ?
    long countByStatus(StudentStatus status);

    // ---- @Query: write the query yourself in JPQL ----
    // JPQL looks like SQL but uses the Java class and field names (Student, s.name),
    // not the table and column names.

    /**
     * Search + filter + pagination in one query.
     * Each filter is optional: if a parameter is null, that condition is skipped.
     */
    @Query("""
            SELECT s FROM Student s
            WHERE (:keyword IS NULL
                   OR LOWER(s.name)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:course IS NULL OR s.course = :course)
              AND (:status IS NULL OR s.status = :status)
            """)
    Page<Student> search(@Param("keyword") String keyword,
                         @Param("course") String course,
                         @Param("status") StudentStatus status,
                         Pageable pageable);

    // All different course names, for the filter dropdown
    @Query("SELECT DISTINCT s.course FROM Student s ORDER BY s.course")
    List<String> findAllCourses();

    // "SELECT new ..." builds a CourseCount object for each row
    @Query("""
            SELECT new com.example.demo.student.CourseCount(s.course, COUNT(s))
            FROM Student s
            GROUP BY s.course
            ORDER BY COUNT(s) DESC
            """)
    List<CourseCount> countByCourse();

    @Query("SELECT COALESCE(AVG(s.age), 0) FROM Student s")
    double averageAge();
}
