package com.example.demo.student;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/**
 * LAYER 1 — ENTITY
 * An @Entity is a Java class that maps to a database table.
 * Each field becomes a column; each object becomes a row.
 *
 * The jakarta.validation annotations (@NotBlank, @Email, ...) are RULES.
 * When the controller receives a Student with @Valid, Spring checks these
 * rules first and rejects bad data with HTTP 400 before your code runs.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id                                                 // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database auto-generates 1, 2, 3...
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    // Optional: if given, it must look like a phone number (7–15 digits, +, -, spaces)
    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Phone must be 7-15 digits")
    private String phone;

    @NotBlank(message = "Course is required")
    private String course;

    // @Enumerated(STRING) stores "ACTIVE" in the database instead of 0, 1, 2
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.ACTIVE;

    @PastOrPresent(message = "Enrollment date cannot be in the future")
    private LocalDate enrollmentDate;

    // JPA requires a no-argument constructor
    public Student() {
    }

    public Student(String name, String email, Integer age, String phone, String course,
            StudentStatus status, LocalDate enrollmentDate) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.phone = phone;
        this.course = course;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
