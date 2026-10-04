package com.example.demo.student;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * LAYER 3 — SERVICE (business logic)
 * The controller should not talk to the database directly.
 * Rules like "email must be unique" or "student must exist" live here.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Dependency Injection: Spring sees this constructor and automatically
    // passes in the StudentRepository it created. You never write "new StudentRepository()".
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // C — Create
    public Student createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }
        student.setId(null); // ignore any id sent by the client; the database chooses it
        if (student.getEnrollmentDate() == null) {
            student.setEnrollmentDate(LocalDate.now());
        }
        return studentRepository.save(student);
    }

    // Fields the client is allowed to sort by
    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "name", "email", "age", "course", "status", "enrollmentDate");

    // R — Read many: search, filter, sort and paginate
    public PageResponse<Student> searchStudents(String keyword, String course,
            StudentStatus status, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException("Cannot sort by '" + order.getProperty() + "'");
            }
        }
        return PageResponse.from(
                studentRepository.search(blankToNull(keyword), blankToNull(course), status, pageable));
    }

    // R — Read one
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    // U — Update (replace all fields)
    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id); // throws 404 if missing
        if (studentRepository.existsByEmailAndIdNot(updated.getEmail(), id)) {
            throw new DuplicateEmailException(updated.getEmail());
        }
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setAge(updated.getAge());
        existing.setPhone(updated.getPhone());
        existing.setCourse(updated.getCourse());
        existing.setStatus(updated.getStatus());
        if (updated.getEnrollmentDate() != null) {
            existing.setEnrollmentDate(updated.getEnrollmentDate());
        }
        return studentRepository.save(existing); // save() on an existing id = UPDATE
    }

    // U — Partial update: change only the status
    // @Transactional: changes to "student" are saved automatically when the method ends
    @Transactional
    public Student updateStatus(Long id, StudentStatus status) {
        Student student = getStudentById(id);
        student.setStatus(status);
        return student;
    }

    // D — Delete
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    public List<String> getAllCourses() {
        return studentRepository.findAllCourses();
    }

    public StudentStats getStats() {
        return new StudentStats(
                studentRepository.count(),
                studentRepository.countByStatus(StudentStatus.ACTIVE),
                studentRepository.countByStatus(StudentStatus.GRADUATED),
                studentRepository.countByStatus(StudentStatus.SUSPENDED),
                Math.round(studentRepository.averageAge() * 10) / 10.0, // 1 decimal place
                studentRepository.countByCourse());
    }

    // "" or "   " from a search box should mean "no filter"
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
