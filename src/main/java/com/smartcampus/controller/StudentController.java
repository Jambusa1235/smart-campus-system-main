package com.smartcampus.controller;

import com.smartcampus.model.*;
import com.smartcampus.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final AcademicService academicService;
    private final AttendanceService attendanceService;
    private final GradingService gradingService;
    private final ReportService reportService;
    private final UserService userService;

    public StudentController(StudentService studentService,
                             AcademicService academicService,
                             AttendanceService attendanceService,
                             GradingService gradingService,
                             ReportService reportService,
                             UserService userService) {
        this.studentService = studentService;
        this.academicService = academicService;
        this.attendanceService = attendanceService;
        this.gradingService = gradingService;
        this.reportService = reportService;
        this.userService = userService;
    }

    private Student getCurrentStudent(Authentication auth) {
        return studentService.getStudentByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Student profile not found for user: " + auth.getName()));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseRegistration> currentRegistrations = (currentYear != null && currentSemester != null)
                ? studentService.getRegistrationsForStudentInTerm(student, currentYear, currentSemester)
                : Collections.emptyList();

        double semesterGPA = (currentYear != null && currentSemester != null)
                ? gradingService.calculateSemesterGPA(student, currentYear, currentSemester)
                : 0.0;
        double cgpa = gradingService.calculateCumulativeGPA(student);
        String standing = gradingService.getAcademicStanding(cgpa);

        // Course attendance percentages
        Map<Long, Double> attendanceMap = new HashMap<>();
        for (CourseRegistration reg : currentRegistrations) {
            attendanceMap.put(reg.getCourse().getId(),
                    attendanceService.calculateAttendancePercentage(student, reg.getCourse()));
        }

        model.addAttribute("student", student);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);
        model.addAttribute("registrations", currentRegistrations);
        model.addAttribute("semesterGPA", semesterGPA);
        model.addAttribute("cgpa", cgpa);
        model.addAttribute("standing", standing);
        model.addAttribute("attendanceMap", attendanceMap);

        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
            return "redirect:/student/profile";
        }
        if (newPassword.trim().length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters long!");
            return "redirect:/student/profile";
        }
        Student student = getCurrentStudent(auth);
        userService.updatePassword(student.getUser().getId(), newPassword.trim());
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
        return "redirect:/student/profile";
    }

    @GetMapping("/courses")
    public String registeredCourses(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseRegistration> currentRegistrations = (currentYear != null && currentSemester != null)
                ? studentService.getRegistrationsForStudentInTerm(student, currentYear, currentSemester)
                : Collections.emptyList();

        List<CourseRegistration> allRegistrations = studentService.getRegistrationsForStudent(student);

        int totalCredits = currentRegistrations.stream()
                .mapToInt(r -> r.getCourse().getCreditUnits()).sum();

        model.addAttribute("student", student);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);
        model.addAttribute("currentRegistrations", currentRegistrations);
        model.addAttribute("allRegistrations", allRegistrations);
        model.addAttribute("totalCredits", totalCredits);

        return "student/registered-courses";
    }

    @GetMapping("/courses/register")
    public String courseRegistrationPortal(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        if (currentYear == null || currentSemester == null) {
            model.addAttribute("errorMessage", "Course registration is currently closed (no active semester).");
            return "student/course-registration";
        }

        // Available courses for student's program
        List<Course> allProgramCourses = academicService.getCoursesByProgram(student.getProgram());

        // Already registered course IDs
        List<CourseRegistration> registered = studentService
                .getRegistrationsForStudentInTerm(student, currentYear, currentSemester);
        Set<Long> registeredCourseIds = registered.stream()
                .map(r -> r.getCourse().getId()).collect(Collectors.toSet());

        List<Course> availableCourses = allProgramCourses.stream()
                .filter(c -> !registeredCourseIds.contains(c.getId()))
                .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);
        model.addAttribute("availableCourses", availableCourses);
        model.addAttribute("registered", registered);

        return "student/course-registration";
    }

    @PostMapping("/courses/register/save")
    public String registerCourses(@RequestParam(value = "courseIds", required = false) List<Long> courseIds,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        if (courseIds == null || courseIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one course to register.");
            return "redirect:/student/courses/register";
        }

        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElseThrow();
        Semester currentSemester = academicService.getCurrentSemester().orElseThrow();

        int registeredCount = 0;
        for (Long cId : courseIds) {
            Course course = academicService.getCourseById(cId).orElse(null);
            if (course != null) {
                try {
                    studentService.registerCourse(student, course, currentYear, currentSemester);
                    registeredCount++;
                } catch (Exception ignored) {}
            }
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Successfully registered for " + registeredCount + " course(s)!");
        return "redirect:/student/courses";
    }

    @GetMapping("/courses/drop/{regId}")
    public String dropCourse(@PathVariable Long regId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        Student student = getCurrentStudent(auth);
        studentService.getRegistrationById(regId).ifPresent(reg -> {
            if (reg.getStudent().getId().equals(student.getId())) {
                studentService.dropCourse(regId);
                redirectAttributes.addFlashAttribute("successMessage", "Course dropped successfully.");
            }
        });
        return "redirect:/student/courses";
    }

    @GetMapping("/attendance")
    public String viewAttendance(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseRegistration> currentRegistrations = (currentYear != null && currentSemester != null)
                ? studentService.getRegistrationsForStudentInTerm(student, currentYear, currentSemester)
                : Collections.emptyList();

        Map<Long, Double> attendanceRateMap = new HashMap<>();
        Map<Long, List<Attendance>> courseAttendanceDetails = new HashMap<>();

        for (CourseRegistration reg : currentRegistrations) {
            Course c = reg.getCourse();
            double rate = attendanceService.calculateAttendancePercentage(student, c);
            attendanceRateMap.put(c.getId(), rate);
            courseAttendanceDetails.put(c.getId(), attendanceService.getAttendanceForStudentAndCourse(student, c));
        }

        model.addAttribute("student", student);
        model.addAttribute("registrations", currentRegistrations);
        model.addAttribute("attendanceRateMap", attendanceRateMap);
        model.addAttribute("courseAttendanceDetails", courseAttendanceDetails);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);

        return "student/attendance";
    }

    @GetMapping("/results")
    public String viewResults(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<Grade> currentGrades = (currentYear != null && currentSemester != null)
                ? gradingService.getGradesForStudentInTerm(student, currentYear, currentSemester)
                : Collections.emptyList();

        double semesterGPA = (currentYear != null && currentSemester != null)
                ? gradingService.calculateSemesterGPA(student, currentYear, currentSemester)
                : 0.0;
        double cgpa = gradingService.calculateCumulativeGPA(student);
        String standing = gradingService.getAcademicStanding(cgpa);

        model.addAttribute("student", student);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);
        model.addAttribute("grades", currentGrades);
        model.addAttribute("semesterGPA", semesterGPA);
        model.addAttribute("cgpa", cgpa);
        model.addAttribute("standing", standing);

        return "student/results";
    }

    @GetMapping("/transcript")
    public String viewTranscript(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        ReportService.StudentTranscript transcript = reportService.generateStudentTranscript(student);

        model.addAttribute("student", student);
        model.addAttribute("transcript", transcript);

        return "student/transcript";
    }
}
