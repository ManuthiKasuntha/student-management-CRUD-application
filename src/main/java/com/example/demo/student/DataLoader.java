package com.example.demo.student;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * A CommandLineRunner runs ONCE, right after the application starts.
 * We use it to insert sample students, so the app is not empty every time
 * you restart (H2 is in-memory and forgets everything on shutdown).
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public DataLoader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() > 0) {
            return; // data already exists, don't add duplicates
        }

        studentRepository.saveAll(List.of(
                new Student("Kasun Perera", "kasun@example.com", 21, "0771234567", "Software Engineering", StudentStatus.ACTIVE, LocalDate.of(2024, 2, 1)),
                new Student("Nimali Silva", "nimali@example.com", 22, "0719876543", "Data Science", StudentStatus.ACTIVE, LocalDate.of(2023, 9, 15)),
                new Student("Tharindu Fernando", "tharindu@example.com", 24, "0765554433", "Software Engineering", StudentStatus.GRADUATED, LocalDate.of(2021, 2, 1)),
                new Student("Ishara Jayasinghe", "ishara@example.com", 20, null, "Cyber Security", StudentStatus.ACTIVE, LocalDate.of(2025, 2, 1)),
                new Student("Dilan Wickramasinghe", "dilan@example.com", 23, "0751112233", "Data Science", StudentStatus.SUSPENDED, LocalDate.of(2022, 9, 1)),
                new Student("Sachini Bandara", "sachini@example.com", 19, "0782223344", "Business Management", StudentStatus.ACTIVE, LocalDate.of(2025, 9, 1)),
                new Student("Ravindu Gunawardena", "ravindu@example.com", 25, "0703334455", "Software Engineering", StudentStatus.GRADUATED, LocalDate.of(2020, 9, 1)),
                new Student("Hiruni Rathnayake", "hiruni@example.com", 21, null, "Cyber Security", StudentStatus.ACTIVE, LocalDate.of(2024, 9, 1)),
                new Student("Pasan Kumara", "pasan@example.com", 22, "0774445566", "Business Management", StudentStatus.ACTIVE, LocalDate.of(2023, 2, 1)),
                new Student("Kavindi Herath", "kavindi@example.com", 20, "0725556677", "Data Science", StudentStatus.ACTIVE, LocalDate.of(2025, 2, 1)),
                new Student("Lahiru Senanayake", "lahiru@example.com", 26, "0716667788", "Cyber Security", StudentStatus.GRADUATED, LocalDate.of(2019, 9, 1)),
                new Student("Amaya Dissanayake", "amaya@example.com", 18, "0767778899", "Software Engineering", StudentStatus.ACTIVE, LocalDate.of(2026, 2, 1))));
    }
}
