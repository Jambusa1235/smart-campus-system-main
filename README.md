# Smart Campus Student Management and Academic Information System

> **Course Work Project**: Design and Development of a Smart Campus Student Management and Academic Information System Using Java  
> **Target Framework**: Java Spring Boot 3.x with Spring Data JPA, Spring Security, Thymeleaf & Bootstrap 5.3  
> **Database**: Embedded / Persistent File H2 (zero setup for evaluators) + MySQL Profile Ready  
> **Submission Deadline**: 25 Sept 2026 (e-campus using GitHub Link)

---

## 1. Project Overview

Universities and tertiary institutions manage complex student academic workflows—including program enrollments, lecturer allocations, lecture session attendance, coursework/examination scoring, GPA/CGPA computation, and transcript issuance.

This project delivers an integrated, centralized, and role-based enterprise web application that streamlines and automates all academic operations for **Administrators**, **Lecturers**, and **Students**.

---

## 2. Technology Stack

- **Core Language**: Java (JDK 17 or higher)
- **Backend Framework**: Spring Boot 3.3.3
  - `spring-boot-starter-web` (MVC REST Controllers & Dispatcher)
  - `spring-boot-starter-data-jpa` (Hibernate ORM & Repositories)
  - `spring-boot-starter-security` (Role-Based Authorization & BCrypt Password Hashing)
  - `spring-boot-starter-thymeleaf` (Server-Side Template Engine)
  - `spring-boot-starter-validation` (Jakarta Validation)
- **Databases**:
  - **H2 File Database** (`./data/campusdb`): Default zero-configuration database that automatically creates and persists data without requiring any external database server installation.
  - **MySQL 8.x**: Optional profile configured in `application-mysql.properties` for production deployment.
- **Frontend & UI**:
  - HTML5 & CSS3
  - Bootstrap 5.3 (Modern Responsive UI)
  - Bootstrap Icons
  - Custom responsive layout with sidebar navigation, alert flashes, and printable CSS for transcripts.

---

## 3. Specific Objectives Implementation Checklist

| # | Course Work Requirement | Implemented Feature / Component | Status |
|---|-------------------------|---------------------------------|:------:|
| 1 | Register and manage student information | `StudentService`, `AdminController` (`/admin/students`), biodata form | ✅ Complete |
| 2 | Manage academic programs and departments | `Department` and `Program` entities, CRUD UI in Admin portal | ✅ Complete |
| 3 | Manage courses and course units | `Course` entity with credit units, year level, semester offered | ✅ Complete |
| 4 | Assign lecturers to courses | `CourseAssignment` entity, assignment manager (`/admin/assignments`) | ✅ Complete |
| 5 | Register students for courses | `StudentController` (`/student/courses/register`), semester registration portal | ✅ Complete |
| 6 | Record student attendance | `AttendanceService`, session register with Present, Late, Absent, Excused | ✅ Complete |
| 7 | Record coursework and examination marks | `LecturerController` (`/lecturer/courses/{id}/marks`), tabular marks entry | ✅ Complete |
| 8 | Automatically calculate student grades | `GradingService`: CW (40) + Exam (60) = 100%, Letter Grades (A–F), Grade Points | ✅ Complete |
| 9 | Generate student transcripts | `ReportService` & `/student/transcript`: official printable/PDF transcript layout | ✅ Complete |
| 10 | Monitor academic performance | CGPA/GPA analytics, standing evaluation (First Class, Upper, etc.) | ✅ Complete |
| 11 | Generate academic reports | Course performance reports (averages, pass/fail rates) & Admin summary reports | ✅ Complete |
| 12 | Provide role-based authentication | Spring Security 6 with `ROLE_ADMIN`, `ROLE_LECTURER`, `ROLE_STUDENT` | ✅ Complete |
| 13 | Maintain secure student records | BCrypt password hashing, session cookies, and route security filters | ✅ Complete |
| 14 | Provide dashboards for different users | Dedicated tailored dashboards for Admin, Lecturer, and Student | ✅ Complete |

---

## 4. Demo Login Credentials

The system automatically initializes sample demo data on first startup via `DataInitializer.java`:

| Role | Username | Password | Profile Details |
|------|----------|----------|-----------------|
| **Administrator** | `admin` | `admin123` | System Academic Administrator |
| **Lecturer** | `dr.smith` | `password123` | Dr. Alan Smith (Computer Science Dept, LEC001) |
| **Lecturer** | `prof.johnson` | `password123` | Prof. Sarah Johnson (Computer Science Dept, LEC002) |
| **Student** | `std001` | `password123` | Odoch joel (BSc Computer Science, STD/2025/001) |
| **Student** | `std002` | `password123` | Alice Mary Smith (BSc Computer Science, STD/2025/002) |

*(Note: The login page includes 1-click autofill buttons for these test accounts for fast evaluator testing).*

---

## 5. How to Run the Application

### Prerequisites
- **Java Development Kit (JDK)**: Version 17 or higher (`java -version`)
- **Apache Maven**: Version 3.8+ (or use the included Maven wrapper)

### Step 1: Open Terminal in Project Directory
```bash
cd smart-campus-system
```

### Step 2: Build and Run
Using standard Maven:
```bash
mvn spring-boot:run
```
*(Or with Maven Wrapper on Windows)*:
```cmd
mvnw.cmd spring-boot:run
```

### Step 3: Access the System
- **Web Application Portal**: [http://localhost:8080](http://localhost:8080)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:file:./data/campusdb`
  - User Name: `sa`
  - Password: *(leave blank)*

### Optional: Running with MySQL
To run with a local MySQL server instead of the default H2 database:
1. Ensure MySQL server is running on port 3306.
2. Run with the `mysql` profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 6. System Workflows & User Guides

### A. Administrator Workflow (`/admin/dashboard`)
1. **Academic Setup**: Navigate to **Academic Sessions** to define academic years (e.g. `2025/2026`) and semesters, setting the active term.
2. **Departments & Programs**: Create faculty departments and degree programs (e.g. `BSc Computer Science`, 3 Years).
3. **Course Units**: Add courses with credit units (CU), syllabus details, and designated year/semester levels.
4. **Course Allocation**: Assign courses to lecturers for the current active semester.
5. **Staff & Student Management**: Register new students and teaching staff with auto-generated system accounts.

### B. Lecturer Workflow (`/lecturer/dashboard`)
1. **Teaching Roster**: View all courses assigned to you for the active semester and inspect the enrolled student roster.
2. **Attendance Tracker**: Select a lecture date, enter the lecture topic, and quickly mark student attendance status (Present, Late, Absent, Excused).
3. **Marks Entry**: Open the marks sheet to enter coursework marks (0–40) and final exam marks (0–60). Total marks, letter grades (A–F), and grade points (0.0–5.0) are calculated in real time.
4. **Course Analytics**: Generate course performance reports showing class average, pass rate percentage, and pass/fail counts.

### C. Student Workflow (`/student/dashboard`)
1. **Course Registration**: Browse available courses for your enrolled program and register for the current semester with a single click.
2. **Registered Courses**: View current course enrollments and total credit load.
3. **Attendance Verification**: Check session-by-session attendance and verify that your attendance percentage meets the **75% examination threshold**.
4. **Results & GPA**: View coursework scores, exam scores, letter grades, and semester GPA.
5. **Official Academic Transcript**: View and print/download the certified university academic transcript with full semester breakdowns, credits earned, cumulative GPA (CGPA), and official degree classification.

---

## 7. Project Structure

```
smart-campus-system/
├── pom.xml                                   # Maven dependencies & build configuration
├── README.md                                 # Full documentation & submission guide
└── src/
    └── main/
        ├── java/com/smartcampus/
        │   ├── SmartCampusApplication.java   # Main Spring Boot starter
        │   ├── config/                       # Security & Data Seeder
        │   │   ├── SecurityConfig.java
        │   │   ├── CustomAuthenticationSuccessHandler.java
        │   │   └── DataInitializer.java
        │   ├── model/                        # JPA Entities
        │   │   ├── Role.java
        │   │   ├── User.java
        │   │   ├── Department.java
        │   │   ├── Program.java
        │   │   ├── AcademicYear.java
        │   │   ├── Semester.java
        │   │   ├── Course.java
        │   │   ├── Lecturer.java
        │   │   ├── Student.java
        │   │   ├── CourseAssignment.java
        │   │   ├── CourseRegistration.java
        │   │   ├── Attendance.java
        │   │   └── Grade.java
        │   ├── repository/                   # Spring Data JPA Repositories
        │   ├── service/                      # Core Business Logic & Grading
        │   │   ├── UserService.java
        │   │   ├── AcademicService.java
        │   │   ├── StudentService.java
        │   │   ├── LecturerService.java
        │   │   ├── AttendanceService.java
        │   │   ├── GradingService.java
        │   │   └── ReportService.java
        │   └── controller/                   # Spring MVC Web Controllers
        │       ├── AuthController.java
        │       ├── AdminController.java
        │       ├── LecturerController.java
        │       └── StudentController.java
        └── resources/
            ├── application.properties        # File H2 DB configuration
            ├── application-mysql.properties  # Optional MySQL configuration
            ├── static/
            │   ├── css/custom.css            # Campus styling & print transcript rules
            │   └── js/app.js                 # Autofill & live grade calculation
            └── templates/                    # Thymeleaf HTML Templates
                ├── layout/base.html          # Sidebar & responsive shell
                ├── auth/login.html           # Authentication portal
                ├── admin/*.html              # Administrator management views
                ├── lecturer/*.html           # Teaching & grading views
                └── student/*.html            # Registration & transcript views
```
