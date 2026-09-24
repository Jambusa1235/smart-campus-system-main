package com.smartcampus.controller;

import com.smartcampus.model.*;
import com.smartcampus.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final LecturerService lecturerService;
    private final AcademicService academicService;
    private final UserService userService;

    public AdminController(StudentService studentService,
                           LecturerService lecturerService,
                           AcademicService academicService,
                           UserService userService) {
        this.studentService = studentService;
        this.lecturerService = lecturerService;
        this.academicService = academicService;
        this.userService = userService;
    }

    // --- Dashboard ---
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentService.getTotalStudentCount());
        model.addAttribute("totalLecturers", lecturerService.getTotalLecturerCount());
        model.addAttribute("totalCourses", academicService.getAllCourses().size());
        model.addAttribute("totalDepartments", academicService.getAllDepartments().size());
        model.addAttribute("currentYear", academicService.getCurrentAcademicYear().orElse(null));
        model.addAttribute("currentSemester", academicService.getCurrentSemester().orElse(null));
        model.addAttribute("recentStudents", studentService.getAllStudents());
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("programs", academicService.getAllPrograms());
        return "admin/dashboard";
    }

    // --- Students Management ---
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("programs", academicService.getAllPrograms());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute Student student,
                              @RequestParam(value = "rawPassword", required = false) String rawPassword,
                              RedirectAttributes redirectAttributes) {
        try {
            if (student.getId() == null) {
                String assignedPassword = studentService.registerStudent(student, rawPassword);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Student " + student.getFullName() + " registered successfully! Login Password: " + assignedPassword);
                redirectAttributes.addFlashAttribute("generatedPassword", assignedPassword);
                redirectAttributes.addFlashAttribute("newUsername", student.getRegistrationNumber());
            } else {
                studentService.updateStudent(student);
                redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/students/new";
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/reset-password/{id}")
    public String resetStudentPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
            String newPass = userService.resetUserPassword(student.getUser().getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset for " + student.getFullName() + " (" + student.getRegistrationNumber() + ")! New Password: " + newPass);
            redirectAttributes.addFlashAttribute("generatedPassword", newPass);
            redirectAttributes.addFlashAttribute("newUsername", student.getRegistrationNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error resetting password: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        model.addAttribute("programs", academicService.getAllPrograms());
        return "admin/student-form";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete student with active enrollments.");
        }
        return "redirect:/admin/students";
    }

    // --- Lecturers Management ---
    @GetMapping("/lecturers")
    public String listLecturers(Model model) {
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        return "admin/lecturers";
    }

    @GetMapping("/lecturers/new")
    public String newLecturerForm(Model model) {
        model.addAttribute("lecturer", new Lecturer());
        model.addAttribute("departments", academicService.getAllDepartments());
        return "admin/lecturer-form";
    }

    @PostMapping("/lecturers/save")
    public String saveLecturer(@ModelAttribute Lecturer lecturer,
                               @RequestParam(value = "rawPassword", required = false) String rawPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            if (lecturer.getId() == null) {
                String assignedPassword = lecturerService.registerLecturer(lecturer, rawPassword);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Lecturer " + lecturer.getFullName() + " registered successfully! Login Password: " + assignedPassword);
                redirectAttributes.addFlashAttribute("generatedPassword", assignedPassword);
                redirectAttributes.addFlashAttribute("newUsername", lecturer.getStaffNumber());
            } else {
                lecturerService.updateLecturer(lecturer);
                redirectAttributes.addFlashAttribute("successMessage", "Lecturer updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/lecturers/new";
        }
        return "redirect:/admin/lecturers";
    }

    @GetMapping("/lecturers/reset-password/{id}")
    public String resetLecturerPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Lecturer lecturer = lecturerService.getLecturerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid lecturer Id:" + id));
            String newPass = userService.resetUserPassword(lecturer.getUser().getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset for " + lecturer.getFullName() + " (" + lecturer.getStaffNumber() + ")! New Password: " + newPass);
            redirectAttributes.addFlashAttribute("generatedPassword", newPass);
            redirectAttributes.addFlashAttribute("newUsername", lecturer.getStaffNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error resetting password: " + e.getMessage());
        }
        return "redirect:/admin/lecturers";
    }

    @GetMapping("/lecturers/edit/{id}")
    public String editLecturerForm(@PathVariable Long id, Model model) {
        Lecturer lecturer = lecturerService.getLecturerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lecturer Id:" + id));
        model.addAttribute("lecturer", lecturer);
        model.addAttribute("departments", academicService.getAllDepartments());
        return "admin/lecturer-form";
    }

    @GetMapping("/lecturers/delete/{id}")
    public String deleteLecturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lecturerService.deleteLecturer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Lecturer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete lecturer with assigned courses.");
        }
        return "redirect:/admin/lecturers";
    }

    // --- Departments ---
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("newDepartment", new Department());
        return "admin/departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        academicService.saveDepartment(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department saved successfully!");
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete department with associated programs.");
        }
        return "redirect:/admin/departments";
    }

    // --- Programs ---
    @GetMapping("/programs")
    public String listPrograms(Model model) {
        model.addAttribute("programs", academicService.getAllPrograms());
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("newProgram", new Program());
        return "admin/programs";
    }

    @PostMapping("/programs/save")
    public String saveProgram(@ModelAttribute Program program, RedirectAttributes redirectAttributes) {
        academicService.saveProgram(program);
        redirectAttributes.addFlashAttribute("successMessage", "Program saved successfully!");
        return "redirect:/admin/programs";
    }

    @GetMapping("/programs/delete/{id}")
    public String deleteProgram(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteProgram(id);
            redirectAttributes.addFlashAttribute("successMessage", "Program deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete program with enrolled students.");
        }
        return "redirect:/admin/programs";
    }

    // --- Courses ---
    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("departments", academicService.getAllDepartments());
        model.addAttribute("programs", academicService.getAllPrograms());
        model.addAttribute("newCourse", new Course());
        return "admin/courses";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {
        academicService.saveCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course saved successfully!");
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete course with active registrations.");
        }
        return "redirect:/admin/courses";
    }

    // --- Academic Sessions (Years & Semesters) ---
    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("academicYears", academicService.getAllAcademicYears());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("newYear", new AcademicYear());
        model.addAttribute("newSemester", new Semester());
        return "admin/academic-sessions";
    }

    @PostMapping("/sessions/year/save")
    public String saveYear(@ModelAttribute AcademicYear academicYear, RedirectAttributes redirectAttributes) {
        academicService.saveAcademicYear(academicYear);
        redirectAttributes.addFlashAttribute("successMessage", "Academic Year saved!");
        return "redirect:/admin/sessions";
    }

    @GetMapping("/sessions/year/activate/{id}")
    public String activateYear(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        academicService.setCurrentAcademicYear(id);
        redirectAttributes.addFlashAttribute("successMessage", "Active Academic Year updated!");
        return "redirect:/admin/sessions";
    }

    @PostMapping("/sessions/semester/save")
    public String saveSemester(@ModelAttribute Semester semester, RedirectAttributes redirectAttributes) {
        academicService.saveSemester(semester);
        redirectAttributes.addFlashAttribute("successMessage", "Semester saved!");
        return "redirect:/admin/sessions";
    }

    @GetMapping("/sessions/semester/activate/{id}")
    public String activateSemester(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        academicService.setCurrentSemester(id);
        redirectAttributes.addFlashAttribute("successMessage", "Active Semester updated!");
        return "redirect:/admin/sessions";
    }

    // --- Course Assignments ---
    @GetMapping("/assignments")
    public String listAssignments(Model model) {
        model.addAttribute("assignments", academicService.getAllCourseAssignments());
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        model.addAttribute("academicYears", academicService.getAllAcademicYears());
        model.addAttribute("semesters", academicService.getAllSemesters());
        model.addAttribute("currentYear", academicService.getCurrentAcademicYear().orElse(null));
        model.addAttribute("currentSemester", academicService.getCurrentSemester().orElse(null));
        return "admin/course-assignments";
    }

    @PostMapping("/assignments/save")
    public String saveAssignment(@RequestParam Long courseId,
                                 @RequestParam Long lecturerId,
                                 @RequestParam Long academicYearId,
                                 @RequestParam Long semesterId,
                                 RedirectAttributes redirectAttributes) {
        Course course = academicService.getCourseById(courseId).orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerById(lecturerId).orElseThrow();
        AcademicYear year = academicService.getAcademicYearById(academicYearId).orElseThrow();
        Semester sem = academicService.getSemesterById(semesterId).orElseThrow();

        academicService.assignLecturerToCourse(course, lecturer, year, sem);
        redirectAttributes.addFlashAttribute("successMessage", "Course assigned to lecturer successfully!");
        return "redirect:/admin/assignments";
    }

    @GetMapping("/assignments/delete/{id}")
    public String deleteAssignment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        academicService.removeAssignment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Assignment removed!");
        return "redirect:/admin/assignments";
    }

    // --- Reports ---
    @GetMapping("/reports")
    public String systemReports(Model model) {
        model.addAttribute("totalStudents", studentService.getTotalStudentCount());
        model.addAttribute("totalLecturers", lecturerService.getTotalLecturerCount());
        model.addAttribute("programs", academicService.getAllPrograms());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("academicYears", academicService.getAllAcademicYears());
        model.addAttribute("semesters", academicService.getAllSemesters());
        return "admin/reports";
    }
}
