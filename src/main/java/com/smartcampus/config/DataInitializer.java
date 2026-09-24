package com.smartcampus.config;

import com.smartcampus.model.*;
import com.smartcampus.repository.*;
import com.smartcampus.service.GradingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final GradingService gradingService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            ProgramRepository programRepository,
            AcademicYearRepository academicYearRepository,
            SemesterRepository semesterRepository,
            CourseRepository courseRepository,
            LecturerRepository lecturerRepository,
            StudentRepository studentRepository,
            CourseAssignmentRepository courseAssignmentRepository,
            CourseRegistrationRepository courseRegistrationRepository,
            AttendanceRepository attendanceRepository,
            GradeRepository gradeRepository,
            GradingService gradingService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.programRepository = programRepository;
        this.academicYearRepository = academicYearRepository;
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
        this.courseAssignmentRepository = courseAssignmentRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
        this.gradingService = gradingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("==============================================");
        System.out.println(">>> Initializing Smart Campus System...");
        System.out.println("==============================================");

        // =========================================================
        // 1. ADMIN USER
        // =========================================================

        if (userRepository.findByUsername("admin").isEmpty()) {

            User adminUser = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@smartcampus.ac.ug",
                    Role.ROLE_ADMIN
            );

            userRepository.save(adminUser);

            System.out.println(">>> Admin created: admin / admin123");
        }

        // =========================================================
        // 2. DEPARTMENTS
        // =========================================================

        Department csDept = departmentRepository
                .findByCode("CSC212")
                .orElseGet(() ->
                        departmentRepository.save(
                                new Department(
                                        "CSC212",
                                        "Computer Science and Technology",
                                        "Department of Computing and Information Technology"
                                )
                        )
                );

        Department eeDept = departmentRepository
                .findByCode("EE")
                .orElseGet(() ->
                        departmentRepository.save(
                                new Department(
                                        "EE",
                                        "Electrical Engineering",
                                        "Department of Electrical and Electronic Engineering"
                                )
                        )
                );

        Department bisDept = departmentRepository
                .findByCode("BIS")
                .orElseGet(() ->
                        departmentRepository.save(
                                new Department(
                                        "BIS",
                                        "Business Information Technology",
                                        "Department of Business and Information Systems"
                                )
                        )
                );

        // =========================================================
        // 3. PROGRAMS
        // =========================================================

        Program bcs = programRepository
                .findByCode("BCS")
                .orElseGet(() ->
                        programRepository.save(
                                new Program(
                                        "BCS",
                                        "Bachelor of Science in Computer Science",
                                        csDept,
                                        3,
                                        "Bachelor"
                                )
                        )
                );

        Program bse = programRepository
                .findByCode("BSE")
                .orElseGet(() ->
                        programRepository.save(
                                new Program(
                                        "BSE",
                                        "Bachelor of Science in Software Engineering",
                                        csDept,
                                        4,
                                        "Bachelor"
                                )
                        )
                );

        Program bbit = programRepository
                .findByCode("BBIT")
                .orElseGet(() ->
                        programRepository.save(
                                new Program(
                                        "BBIT",
                                        "Bachelor of Business Information Technology",
                                        bisDept,
                                        3,
                                        "Bachelor"
                                )
                        )
                );

        // =========================================================
        // 4. ACADEMIC YEAR
        // =========================================================

        AcademicYear ay2025 = academicYearRepository
                .findByName("2025/2026")
                .orElseGet(() ->
                        academicYearRepository.save(
                                new AcademicYear(
                                        "2025/2026",
                                        true
                                )
                        )
                );

        // =========================================================
        // 5. SEMESTERS
        // =========================================================

        Semester sem1 = semesterRepository
                .findByNameAndAcademicYear("Semester 1", ay2025)
                .orElseGet(() ->
                        semesterRepository.save(
                                new Semester(
                                        "Semester 1",
                                        ay2025,
                                        LocalDate.of(2025, 9, 1),
                                        LocalDate.of(2026, 1, 31),
                                        true
                                )
                        )
                );

        Semester sem2 = semesterRepository
                .findByNameAndAcademicYear("Semester 2", ay2025)
                .orElseGet(() ->
                        semesterRepository.save(
                                new Semester(
                                        "Semester 2",
                                        ay2025,
                                        LocalDate.of(2026, 2, 15),
                                        LocalDate.of(2026, 6, 30),
                                        false
                                )
                        )
                );

        // =========================================================
        // 6. COURSES
        // =========================================================

        Course cs201 = courseRepository
                .findByCode("CS201")
                .orElseGet(() ->
                        courseRepository.save(
                                new Course(
                                        "CS201",
                                        "Object-Oriented Programming",
                                        4,
                                        csDept,
                                        bcs,
                                        1,
                                        1,
                                        "Classes, objects, inheritance, polymorphism, abstraction and encapsulation."
                                )
                        )
                );

        Course cs202 = courseRepository
                .findByCode("CS202")
                .orElseGet(() ->
                        courseRepository.save(
                                new Course(
                                        "CS202",
                                        "Software Engineering Principles",
                                        3,
                                        csDept,
                                        bse,
                                        1,
                                        1,
                                        "Software development methodologies, requirements analysis, design and testing."
                                )
                        )
                );

        Course cs203 = courseRepository
                .findByCode("CS203")
                .orElseGet(() ->
                        courseRepository.save(
                                new Course(
                                        "CS203",
                                        "Database Administration",
                                        3,
                                        csDept,
                                        bcs,
                                        2,
                                        1,
                                        "Database security, backup, recovery, administration and performance management."
                                )
                        )
                );

        Course cs204 = courseRepository
                .findByCode("CS204")
                .orElseGet(() ->
                        courseRepository.save(
                                new Course(
                                        "CS204",
                                        "Mobile Application Development",
                                        4,
                                        csDept,
                                        bse,
                                        2,
                                        1,
                                        "Design and development of mobile applications using modern programming techniques."
                                )
                        )
                );

        Course cs205 = courseRepository
                .findByCode("CS205")
                .orElseGet(() ->
                        courseRepository.save(
                                new Course(
                                        "CS205",
                                        "Computer Security",
                                        3,
                                        csDept,
                                        bcs,
                                        2,
                                        1,
                                        "Network security, authentication, encryption, threats and information security."
                                )
                        )
                );

        // =========================================================
        // 7. LECTURERS
        // =========================================================

        Lecturer lect1 = lecturerRepository
                .findByUserUsername("lect.sarah")
                .orElseGet(() -> {

                    User lecturerUser = userRepository
                            .findByUsername("lect.sarah")
                            .orElseGet(() ->
                                    userRepository.save(
                                            new User(
                                                    "lect.sarah",
                                                    passwordEncoder.encode("password123"),
                                                    "sarah.namukasa@smartcampus.ac.ug",
                                                    Role.ROLE_LECTURER
                                            )
                                    )
                            );

                    return lecturerRepository.save(
                            new Lecturer(
                                    "LEC101",
                                    lecturerUser,
                                    "Sarah Namukasa",
                                    "Dr.",
                                    "sarah.namukasa@smartcampus.ac.ug",
                                    "+256-700-555111",
                                    csDept,
                                    "Block A, Room 201"
                            )
                    );
                });

        Lecturer lect2 = lecturerRepository
                .findByUserUsername("lect.brian")
                .orElseGet(() -> {

                    User lecturerUser = userRepository
                            .findByUsername("lect.brian")
                            .orElseGet(() ->
                                    userRepository.save(
                                            new User(
                                                    "lect.brian",
                                                    passwordEncoder.encode("password123"),
                                                    "brian.okello@smartcampus.ac.ug",
                                                    Role.ROLE_LECTURER
                                            )
                                    )
                            );

                    return lecturerRepository.save(
                            new Lecturer(
                                    "LEC102",
                                    lecturerUser,
                                    "Mr. Brian Okello",
                                    "Mr.",
                                    "brian.okello@smartcampus.ac.ug",
                                    "+256-700-555222",
                                    csDept,
                                    "Block A, Room 205"
                            )
                    );
                });

        // =========================================================
        // 8. COURSE ASSIGNMENTS
        // =========================================================

        if (courseAssignmentRepository.count() == 0) {

            courseAssignmentRepository.save(
                    new CourseAssignment(
                            cs201,
                            lect1,
                            ay2025,
                            sem1
                    )
            );

            courseAssignmentRepository.save(
                    new CourseAssignment(
                            cs202,
                            lect1,
                            ay2025,
                            sem1
                    )
            );

            courseAssignmentRepository.save(
                    new CourseAssignment(
                            cs203,
                            lect2,
                            ay2025,
                            sem1
                    )
            );

            courseAssignmentRepository.save(
                    new CourseAssignment(
                            cs204,
                            lect2,
                            ay2025,
                            sem1
                    )
            );

            courseAssignmentRepository.save(
                    new CourseAssignment(
                            cs205,
                            lect1,
                            ay2025,
                            sem1
                    )
            );
        }

        // =========================================================
        // 9. STUDENTS
        // =========================================================

        Student student1 = studentRepository
                .findByUserUsername("std101")
                .orElseGet(() -> {

                    User studentUser = userRepository
                            .findByUsername("std101")
                            .orElseGet(() ->
                                    userRepository.save(
                                            new User(
                                                    "std101",
                                                    passwordEncoder.encode("password123"),
                                                    "std101@smartcampus.ac.ug",
                                                    Role.ROLE_STUDENT
                                            )
                                    )
                            );

                    return studentRepository.save(
                            new Student(
                                    "STD/2025/101",
                                    studentUser,
                                    "Daniel Okello",
                                    "Male",
                                    LocalDate.of(2003, 8, 12),
                                    "+256-750-111111",
                                    "Kampala Student Residence",
                                    bcs,
                                    1,
                                    1,
                                    "ACTIVE"
                            )
                    );
                });

        Student student2 = studentRepository
                .findByUserUsername("std102")
                .orElseGet(() -> {

                    User studentUser = userRepository
                            .findByUsername("std102")
                            .orElseGet(() ->
                                    userRepository.save(
                                            new User(
                                                    "std102",
                                                    passwordEncoder.encode("password123"),
                                                    "std102@smartcampus.ac.ug",
                                                    Role.ROLE_STUDENT
                                            )
                                    )
                            );

                    return studentRepository.save(
                            new Student(
                                    "STD/2025/102",
                                    studentUser,
                                    "Grace Atim",
                                    "Female",
                                    LocalDate.of(2004, 4, 25),
                                    "+256-750-222222",
                                    "University Hostel Block B",
                                    bcs,
                                    1,
                                    1,
                                    "ACTIVE"
                            )
                    );
                });

        // =========================================================
        // 10. COURSE REGISTRATION
        // =========================================================

        if (courseRegistrationRepository.count() == 0) {

            CourseRegistration reg1 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student1,
                                    cs201,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg2 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student1,
                                    cs202,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg3 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student1,
                                    cs203,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg4 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student1,
                                    cs204,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg5 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student2,
                                    cs201,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg6 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student2,
                                    cs202,
                                    ay2025,
                                    sem1
                            )
                    );

            CourseRegistration reg7 =
                    courseRegistrationRepository.save(
                            new CourseRegistration(
                                    student2,
                                    cs205,
                                    ay2025,
                                    sem1
                            )
                    );

            // =====================================================
            // 11. ATTENDANCE
            // =====================================================

            LocalDate today = LocalDate.now();

            attendanceRepository.save(
                    new Attendance(
                            cs201,
                            student1,
                            today.minusDays(7),
                            Attendance.Status.PRESENT,
                            "Classes and Objects",
                            lect1
                    )
            );

            attendanceRepository.save(
                    new Attendance(
                            cs201,
                            student2,
                            today.minusDays(7),
                            Attendance.Status.PRESENT,
                            "Classes and Objects",
                            lect1
                    )
            );

            attendanceRepository.save(
                    new Attendance(
                            cs202,
                            student1,
                            today.minusDays(5),
                            Attendance.Status.PRESENT,
                            "Software Development Life Cycle",
                            lect1
                    )
            );

            attendanceRepository.save(
                    new Attendance(
                            cs202,
                            student2,
                            today.minusDays(5),
                            Attendance.Status.LATE,
                            "Software Development Life Cycle",
                            lect1
                    )
            );

            attendanceRepository.save(
                    new Attendance(
                            cs203,
                            student1,
                            today.minusDays(3),
                            Attendance.Status.PRESENT,
                            "Database Backup and Recovery",
                            lect2
                    )
            );

            // =====================================================
            // 12. SAMPLE GRADES
            // =====================================================

            gradingService.recordMarks(
                    reg1,
                    35.0,
                    50.0,
                    lect1
            ); // Total = 85

            gradingService.recordMarks(
                    reg2,
                    32.0,
                    46.0,
                    lect1
            ); // Total = 78

            gradingService.recordMarks(
                    reg3,
                    30.0,
                    43.0,
                    lect2
            ); // Total = 73

            gradingService.recordMarks(
                    reg4,
                    37.0,
                    51.0,
                    lect2
            ); // Total = 88

            gradingService.recordMarks(
                    reg5,
                    29.0,
                    41.0,
                    lect1
            ); // Total = 70

            gradingService.recordMarks(
                    reg6,
                    33.0,
                    44.0,
                    lect1
            ); // Total = 77

            gradingService.recordMarks(
                    reg7,
                    26.0,
                    37.0,
                    lect1
            ); // Total = 63
        }

        // =========================================================
        // FINISHED
        // =========================================================

        System.out.println("==============================================");
        System.out.println(">>> Smart Campus demo data is ready!");
        System.out.println("==============================================");

        System.out.println("Admin:");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");

        System.out.println();

        System.out.println("Lecturer 1:");
        System.out.println("Username: lect.sarah");
        System.out.println("Password: password123");

        System.out.println();

        System.out.println("Lecturer 2:");
        System.out.println("Username: lect.brian");
        System.out.println("Password: password123");

        System.out.println();

        System.out.println("Student 1:");
        System.out.println("Username: std101");
        System.out.println("Password: password123");

        System.out.println();

        System.out.println("Student 2:");
        System.out.println("Username: std102");
        System.out.println("Password: password123");

        System.out.println("==============================================");
    }
}