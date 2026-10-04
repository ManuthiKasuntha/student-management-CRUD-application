# Student Manager

A full-stack **Student Management System** built with **Spring Boot 4** and a plain **HTML/CSS/JavaScript** frontend.
It provides a REST API for CRUD operations on student records, with search, filtering, sorting, pagination, validation and statistics.

Built as a learning project to practise Spring Boot fundamentals: layered architecture, Spring Data JPA, validation and REST API design.

---

## Features

- **CRUD**: create, view, update and delete students
- **Search** by name or email
- **Filter** by course and status
- **Sort** by any column (ascending / descending)
- **Pagination** (5 / 10 / 20 per page)
- **Quick status update** (Active / Graduated / Suspended) using `PATCH`
- **Statistics**: total students, status counts, average age, students per course
- **Validation** with clear field-level error messages
- **Global error handling**: consistent JSON error responses (400 / 404 / 409)
- **Sample data** loaded automatically on startup
- **Web interface** served by Spring Boot itself

## Tech Stack

| Layer      | Technology                              |
|------------|-----------------------------------------|
| Backend    | Java 17+, Spring Boot 4 (Web MVC)        |
| Data       | Spring Data JPA, Hibernate               |
| Database   | H2 (in-memory)                           |
| Validation | Jakarta Bean Validation                  |
| Frontend   | HTML, CSS, JavaScript (Fetch API)        |
| Build      | Maven (Maven Wrapper included)           |

## Architecture

```
Frontend (HTML / CSS / JS)
        │  HTTP + JSON
        ▼
Controller   →  handles HTTP requests, validates input
        ▼
Service      →  business rules (unique email, student exists)
        ▼
Repository   →  database access (Spring Data JPA)
        ▼
Entity       →  Student  ↔  "students" table
        ▼
H2 Database

GlobalExceptionHandler → converts exceptions into clean JSON error responses
```

## Project Structure

```
src/main/java/com/example/demo/
├── DemoApplication.java            # Application entry point
├── exception/
│   ├── GlobalExceptionHandler.java # Central error handling
│   └── ErrorResponse.java          # Error JSON format
└── student/
    ├── Student.java                # JPA entity + validation rules
    ├── StudentStatus.java          # ACTIVE / GRADUATED / SUSPENDED
    ├── StudentRepository.java      # Database queries
    ├── StudentService.java         # Business logic
    ├── StudentController.java      # REST endpoints
    ├── DataLoader.java             # Sample data on startup
    └── ...                         # DTO records and exceptions

src/main/resources/
├── application.properties          # Configuration
└── static/                         # Frontend (index.html, style.css, app.js)
```

## Getting Started

### Prerequisites
- **Java 17 or newer** (`java -version`)
- No Maven or database installation needed

### Run

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

Then open:

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Web interface |
| http://localhost:8080/api/students | REST API |
| http://localhost:8080/h2-console | Database console (JDBC URL: `jdbc:h2:mem:studentdb`, user: `sa`, no password) |

> The H2 database is in-memory: data resets on every restart, and sample students are loaded again.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/students` | List students (search, filter, sort, paginate) |
| `GET` | `/api/students/{id}` | Get one student |
| `POST` | `/api/students` | Create a student |
| `PUT` | `/api/students/{id}` | Update a student |
| `PATCH` | `/api/students/{id}/status` | Update only the status |
| `DELETE` | `/api/students/{id}` | Delete a student |
| `GET` | `/api/students/courses` | List all course names |
| `GET` | `/api/students/stats` | Get statistics |

**Query parameters for `GET /api/students`:**

| Parameter | Example | Description |
|-----------|---------|-------------|
| `keyword` | `?keyword=kasun` | Search in name and email |
| `course`  | `?course=Data Science` | Filter by course |
| `status`  | `?status=ACTIVE` | Filter by status |
| `page`    | `?page=0` | Page number (starts at 0) |
| `size`    | `?size=10` | Items per page (default 5) |
| `sort`    | `?sort=name,desc` | Sort field and direction |

### Example request

```http
POST /api/students
Content-Type: application/json

{
  "name": "Kasun Perera",
  "email": "kasun@example.com",
  "age": 21,
  "phone": "0771234567",
  "course": "Software Engineering",
  "status": "ACTIVE",
  "enrollmentDate": "2024-02-01"
}
```

### Example error response

```json
{
  "timestamp": "2026-10-04T19:08:38",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Email must be a valid email address",
    "age": "Age must be at least 16"
  }
}
```

More ready-to-run examples are in [`requests.http`](requests.http) (use the VS Code **REST Client** extension).

## Future Improvements

- Course entity with a `@ManyToOne` relationship
- DTOs to separate the API from the database model
- Unit and integration tests
- PostgreSQL for persistent storage
- Authentication with Spring Security
