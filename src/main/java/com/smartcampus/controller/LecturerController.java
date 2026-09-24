package com.smartcampus.controller;

import com.smartcampus.model.*;
import com.smartcampus.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    private final LecturerService lecturerService;
    private final AcademicService academicService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final GradingService gradingService;
    private final ReportService reportService;

    public LecturerController(LecturerService lecturerService,
                              AcademicService academicService,
                              StudentService studentService,
                              AttendanceService attendanceService,
                              GradingService gradingService,
                              ReportService reportService) {
        this.lecturerService = lecturerService;
        this.academicService = academicService;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.gradingService = gradingService;
        this.reportService = reportService;
    }

    private Lecturer getCurrentLecturer(Authentication auth) {
        return lecturerService.getLecturerByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Lecturer profile not found for user: " + auth.getName()));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseAssignment> assignments = academicService.getAssignmentsForLecturer(lecturer);
        List<Course> activeCourses = (currentYear != null && currentSemester != null)
                ? lecturerService.getCoursesTaughtByLecturerInTerm(lecturer, currentYear, currentSemester)
                : Collections.emptyList();

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);
        model.addAttribute("assignments", assignments);
        model.addAttribute("activeCourses", activeCourses);

        return "lecturer/dashboard";
    }

    @GetMapping("/courses")
    public String myCourses(Authentication auth, Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseAssignment> assignments = academicService.getAssignmentsForLecturer(lecturer);

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("assignments", assignments);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);

        return "lecturer/courses";
    }

    // View registered students for an assigned course
    @GetMapping("/courses/{courseId}/students")
    public String viewCourseStudents(@PathVariable Long courseId,
                                     Authentication auth,
                                     Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        Course course = academicService.getCourseById(courseId).orElseThrow();
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseRegistration> registrations = (currentYear != null && currentSemester != null)
                ? studentService.getRegistrationsForCourseInTerm(course, currentYear, currentSemester)
                : Collections.emptyList();

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("course", course);
        model.addAttribute("registrations", registrations);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);

        return "lecturer/roster";
    }

    // Attendance management
    @GetMapping("/courses/{courseId}/attendance")
    public String courseAttendance(@PathVariable Long courseId,
                                   @RequestParam(value = "date", required = false) String dateStr,
                                   Authentication auth,
                                   Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        Course course = academicService.getCourseById(courseId).orElseThrow();
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        LocalDate selectedDate = dateStr != null && !dateStr.isEmpty()
                ? LocalDate.parse(dateStr)
                : LocalDate.now();

        List<CourseRegistration> registrations = studentService
                .getRegistrationsForCourseInTerm(course, currentYear, currentSemester);

        // Fetch existing attendance records for the selected date
        List<Attendance> existingAttendance = attendanceService.getAttendanceForCourseAndDate(course, selectedDate);
        Map<Long, Attendance> attendanceMap = new HashMap<>();
        for (Attendance a : existingAttendance) {
            attendanceMap.put(a.getStudent().getId(), a);
        }

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("course", course);
        model.addAttribute("registrations", registrations);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("attendanceMap", attendanceMap);
        model.addAttribute("statuses", Attendance.Status.values());
        model.addAttribute("allCourseAttendance", attendanceService.getAttendanceForCourse(course));

        return "lecturer/attendance";
    }

    @PostMapping("/courses/{courseId}/attendance/save")
    public String saveAttendance(@PathVariable Long courseId,
                                 @RequestParam("attendanceDate") String dateStr,
                                 @RequestParam(value = "topic", required = false) String topic,
                                 @RequestParam Map<String, String> params,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        Lecturer lecturer = getCurrentLecturer(auth);
        Course course = academicService.getCourseById(courseId).orElseThrow();
        LocalDate date = LocalDate.parse(dateStr);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("status_")) {
                Long studentId = Long.parseLong(entry.getKey().substring("status_".length()));
                Attendance.Status status = Attendance.Status.valueOf(entry.getValue());
                Student student = studentService.getStudentById(studentId).orElse(null);
                if (student != null) {
                    attendanceService.recordAttendance(course, student, date, status, topic, lecturer);
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Attendance recorded successfully for " + date + "!");
        return "redirect:/lecturer/courses/" + courseId + "/attendance?date=" + dateStr;
    }

    // Marks Entry (Coursework + Examination)
    @GetMapping("/courses/{courseId}/marks")
    public String marksEntry(@PathVariable Long courseId,
                             Authentication auth,
                             Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        Course course = academicService.getCourseById(courseId).orElseThrow();
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        List<CourseRegistration> registrations = studentService
                .getRegistrationsForCourseInTerm(course, currentYear, currentSemester);

        Map<Long, Grade> gradeMap = new HashMap<>();
        for (CourseRegistration reg : registrations) {
            gradingService.getGradeForRegistration(reg).ifPresent(g -> gradeMap.put(reg.getId(), g));
        }

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("course", course);
        model.addAttribute("registrations", registrations);
        model.addAttribute("gradeMap", gradeMap);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);

        return "lecturer/marks-entry";
    }

    @PostMapping("/courses/{courseId}/marks/save")
    public String saveMarks(@PathVariable Long courseId,
                            @RequestParam Map<String, String> params,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        Lecturer lecturer = getCurrentLecturer(auth);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("cw_")) {
                Long regId = Long.parseLong(entry.getKey().substring("cw_".length()));
                String cwStr = entry.getValue();
                String examStr = params.get("exam_" + regId);

                Double cw = (cwStr != null && !cwStr.trim().isEmpty()) ? Double.parseDouble(cwStr) : null;
                Double exam = (examStr != null && !examStr.trim().isEmpty()) ? Double.parseDouble(examStr) : null;

                studentService.getRegistrationById(regId).ifPresent(reg -> {
                    gradingService.recordMarks(reg, cw, exam, lecturer);
                });
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Course marks updated and grades calculated successfully!");
        return "redirect:/lecturer/courses/" + courseId + "/marks";
    }

    // Course Performance Report
    @GetMapping("/courses/{courseId}/report")
    public String courseReport(@PathVariable Long courseId,
                               Authentication auth,
                               Model model) {
        Lecturer lecturer = getCurrentLecturer(auth);
        Course course = academicService.getCourseById(courseId).orElseThrow();
        AcademicYear currentYear = academicService.getCurrentAcademicYear().orElse(null);
        Semester currentSemester = academicService.getCurrentSemester().orElse(null);

        ReportService.CoursePerformanceReport report = reportService.generateCourseReport(course, currentYear, currentSemester);

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("course", course);
        model.addAttribute("report", report);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentSemester", currentSemester);

        return "lecturer/course-report";
    }
}
