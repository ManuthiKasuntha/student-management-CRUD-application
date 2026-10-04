package com.example.demo.student;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * LAYER 4 — CONTROLLER (handles HTTP requests)
 * @RestController = every method returns data (JSON), not an HTML page.
 * @RequestMapping  = all URLs in this class start with /api/students
 *
 *   HTTP method   URL                          What it does
 *   POST          /api/students                Create
 *   GET           /api/students                Read many (search, filter, sort, pages)
 *   GET           /api/students/{id}           Read one
 *   PUT           /api/students/{id}           Update all fields
 *   PATCH         /api/students/{id}/status    Update only the status
 *   DELETE        /api/students/{id}           Delete
 *   GET           /api/students/courses        List of course names
 *   GET           /api/students/stats          Summary numbers
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // @Valid = check the rules in Student (@NotBlank, @Email...) before running this method.
    // If a rule fails, GlobalExceptionHandler returns 400 with the error messages.
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        Student saved = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201 Created
    }

    /**
     * Examples:
     *   /api/students                               -> page 0, 5 per page, sorted by id
     *   /api/students?keyword=ka                    -> name or email contains "ka"
     *   /api/students?course=Data Science           -> only that course
     *   /api/students?status=GRADUATED              -> only graduated
     *   /api/students?page=1&size=10&sort=name,desc -> page 2, 10 per page, Z to A
     *
     * @RequestParam(required = false) = the parameter is optional.
     * Pageable is filled by Spring from ?page=&size=&sort= automatically.
     */
    @GetMapping
    public PageResponse<Student> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) StudentStatus status,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.searchStudents(keyword, course, status, pageable);
    }

    // @PathVariable takes {id} from the URL, e.g. /api/students/5 -> id = 5
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    // PATCH = change only part of a resource (here: just the status)
    @PatchMapping("/{id}/status")
    public Student updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return studentService.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/courses")
    public List<String> getCourses() {
        return studentService.getAllCourses();
    }

    @GetMapping("/stats")
    public StudentStats getStats() {
        return studentService.getStats();
    }
}
