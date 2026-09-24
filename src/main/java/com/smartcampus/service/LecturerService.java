package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final UserService userService;

    public LecturerService(LecturerRepository lecturerRepository,
                           CourseAssignmentRepository courseAssignmentRepository,
                           CourseRegistrationRepository courseRegistrationRepository,
                           UserService userService) {
        this.lecturerRepository = lecturerRepository;
        this.courseAssignmentRepository = courseAssignmentRepository;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.userService = userService;
    }

    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    public Optional<Lecturer> getLecturerById(Long id) {
        return lecturerRepository.findById(id);
    }

    public Optional<Lecturer> getLecturerByStaffNumber(String staffNumber) {
        return lecturerRepository.findByStaffNumber(staffNumber);
    }

    public Optional<Lecturer> getLecturerByUsername(String username) {
        Optional<Lecturer> lecturer = lecturerRepository.findByUserUsername(username);
        if (lecturer.isPresent()) {
            return lecturer;
        }
        lecturer = lecturerRepository.findByStaffNumber(username);
        if (lecturer.isPresent()) {
            return lecturer;
        }
        // Fallback self-healing: if User exists with ROLE_LECTURER, auto-generate linked profile
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getRole() == Role.ROLE_LECTURER) {
            User user = userOpt.get();
            Lecturer newLec = new Lecturer(
                    username.toUpperCase(),
                    user,
                    user.getUsername(),
                    "Dr.",
                    user.getEmail(),
                    "+256-700-000000",
                    null,
                    "Faculty Office"
            );
            return Optional.of(lecturerRepository.save(newLec));
        }
        return Optional.empty();
    }

    public String registerLecturer(Lecturer lecturer, String rawPassword) {
        String passwordToUse = (rawPassword != null && !rawPassword.trim().isEmpty())
                ? rawPassword.trim()
                : userService.generatePassword();

        if (lecturer.getUser() == null) {
            String email = lecturer.getEmail() != null ? lecturer.getEmail() :
                    (lecturer.getStaffNumber().toLowerCase().replace("/", "") + "@campus.ac.ug");
            User user = userService.createUser(
                    lecturer.getStaffNumber(),
                    passwordToUse,
                    email,
                    Role.ROLE_LECTURER
            );
            lecturer.setUser(user);
        }
        lecturerRepository.save(lecturer);
        return passwordToUse;
    }

    public Lecturer updateLecturer(Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    public void deleteLecturer(Long id) {
        lecturerRepository.deleteById(id);
    }

    public long getTotalLecturerCount() {
        return lecturerRepository.count();
    }

    public List<Course> getCoursesTaughtByLecturerInTerm(Lecturer lecturer, AcademicYear year, Semester semester) {
        List<CourseAssignment> assignments = courseAssignmentRepository
                .findByLecturerAndAcademicYearAndSemester(lecturer, year, semester);
        return assignments.stream().map(CourseAssignment::getCourse).collect(Collectors.toList());
    }

    public List<Student> getStudentsEnrolledInCourse(Course course, AcademicYear year, Semester semester) {
        List<CourseRegistration> registrations = courseRegistrationRepository
                .findByCourseAndAcademicYearAndSemester(course, year, semester);
        return registrations.stream().map(CourseRegistration::getStudent).collect(Collectors.toList());
    }
}
