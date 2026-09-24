package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final UserService userService;
    private final ProgramRepository programRepository;

    public StudentService(StudentRepository studentRepository,
                          CourseRegistrationRepository courseRegistrationRepository,
                          UserService userService,
                          ProgramRepository programRepository) {
        this.studentRepository = studentRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.userService = userService;
        this.programRepository = programRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRegistrationNumber(String regNumber) {
        return studentRepository.findByRegistrationNumber(regNumber);
    }

    public Optional<Student> getStudentByUsername(String username) {
        Optional<Student> student = studentRepository.findByUserUsername(username);
        if (student.isPresent()) {
            return student;
        }
        student = studentRepository.findByRegistrationNumber(username);
        if (student.isPresent()) {
            return student;
        }
        // Fallback self-healing: if User exists with ROLE_STUDENT, auto-generate linked student profile
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getRole() == Role.ROLE_STUDENT) {
            User user = userOpt.get();
            Program defaultProgram = programRepository.findAll().stream().findFirst().orElse(null);
            Student newStud = new Student(
                    username.toUpperCase(),
                    user,
                    username,
                    "Male",
                    java.time.LocalDate.of(2003, 1, 1),
                    "+256-750-000000",
                    "Campus Hall",
                    defaultProgram,
                    1,
                    1,
                    "ACTIVE"
            );
            return Optional.of(studentRepository.save(newStud));
        }
        return Optional.empty();
    }

    public String registerStudent(Student student, String rawPassword) {
        String passwordToUse = (rawPassword != null && !rawPassword.trim().isEmpty())
                ? rawPassword.trim()
                : userService.generatePassword();

        if (student.getUser() == null) {
            String email = student.getRegistrationNumber().toLowerCase().replace("/", "") + "@campus.ac.ug";
            User user = userService.createUser(
                    student.getRegistrationNumber(),
                    passwordToUse,
                    email,
                    Role.ROLE_STUDENT
            );
            student.setUser(user);
        }
        studentRepository.save(student);
        return passwordToUse;
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public long getTotalStudentCount() {
        return studentRepository.count();
    }

    // --- Course Registration operations ---
    public CourseRegistration registerCourse(Student student, Course course, AcademicYear academicYear, Semester semester) {
        if (courseRegistrationRepository.existsByStudentAndCourseAndAcademicYearAndSemester(student, course, academicYear, semester)) {
            throw new IllegalStateException("Student is already registered for this course in the current term.");
        }
        CourseRegistration reg = new CourseRegistration(student, course, academicYear, semester);
        return courseRegistrationRepository.save(reg);
    }

    public void dropCourse(Long registrationId) {
        courseRegistrationRepository.deleteById(registrationId);
    }

    public List<CourseRegistration> getRegistrationsForStudent(Student student) {
        return courseRegistrationRepository.findByStudent(student);
    }

    public List<CourseRegistration> getRegistrationsForStudentInTerm(Student student, AcademicYear academicYear, Semester semester) {
        return courseRegistrationRepository.findByStudentAndAcademicYearAndSemester(student, academicYear, semester);
    }

    public List<CourseRegistration> getRegistrationsForCourseInTerm(Course course, AcademicYear academicYear, Semester semester) {
        return courseRegistrationRepository.findByCourseAndAcademicYearAndSemester(course, academicYear, semester);
    }

    public Optional<CourseRegistration> getRegistrationById(Long id) {
        return courseRegistrationRepository.findById(id);
    }

    public long getEnrollmentCountForCourse(Course course, AcademicYear academicYear, Semester semester) {
        return courseRegistrationRepository.countByCourseAndAcademicYearAndSemester(course, academicYear, semester);
    }
}
