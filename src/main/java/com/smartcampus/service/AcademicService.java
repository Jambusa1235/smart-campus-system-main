package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AcademicService {

    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;

    public AcademicService(DepartmentRepository departmentRepository,
                           ProgramRepository programRepository,
                           CourseRepository courseRepository,
                           AcademicYearRepository academicYearRepository,
                           SemesterRepository semesterRepository,
                           CourseAssignmentRepository courseAssignmentRepository) {
        this.departmentRepository = departmentRepository;
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
        this.academicYearRepository = academicYearRepository;
        this.semesterRepository = semesterRepository;
        this.courseAssignmentRepository = courseAssignmentRepository;
    }

    // --- Department operations ---
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // --- Program operations ---
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public Optional<Program> getProgramById(Long id) {
        return programRepository.findById(id);
    }

    public List<Program> getProgramsByDepartment(Department department) {
        return programRepository.findByDepartment(department);
    }

    public Program saveProgram(Program program) {
        return programRepository.save(program);
    }

    public void deleteProgram(Long id) {
        programRepository.deleteById(id);
    }

    // --- Course operations ---
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByProgram(Program program) {
        return courseRepository.findByProgram(program);
    }

    public List<Course> getCoursesForProgramAndYearAndSemester(Program program, int year, int semester) {
        return courseRepository.findByProgramAndYearLevelAndSemesterOffered(program, year, semester);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // --- Academic Year operations ---
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    public Optional<AcademicYear> getAcademicYearById(Long id) {
        return academicYearRepository.findById(id);
    }

    public Optional<AcademicYear> getCurrentAcademicYear() {
        return academicYearRepository.findByIsCurrentTrue();
    }

    public AcademicYear saveAcademicYear(AcademicYear academicYear) {
        if (academicYear.isCurrent()) {
            academicYearRepository.findAll().forEach(ay -> {
                if (!ay.getId().equals(academicYear.getId()) && ay.isCurrent()) {
                    ay.setCurrent(false);
                    academicYearRepository.save(ay);
                }
            });
        }
        return academicYearRepository.save(academicYear);
    }

    public void setCurrentAcademicYear(Long id) {
        academicYearRepository.findAll().forEach(ay -> {
            ay.setCurrent(ay.getId().equals(id));
            academicYearRepository.save(ay);
        });
    }

    // --- Semester operations ---
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    public List<Semester> getSemestersByAcademicYear(AcademicYear academicYear) {
        return semesterRepository.findByAcademicYear(academicYear);
    }

    public Optional<Semester> getCurrentSemester() {
        return semesterRepository.findByIsCurrentTrue();
    }

    public Optional<Semester> getSemesterById(Long id) {
        return semesterRepository.findById(id);
    }

    public Semester saveSemester(Semester semester) {
        if (semester.isCurrent()) {
            semesterRepository.findAll().forEach(s -> {
                if (!s.getId().equals(semester.getId()) && s.isCurrent()) {
                    s.setCurrent(false);
                    semesterRepository.save(s);
                }
            });
        }
        return semesterRepository.save(semester);
    }

    public void setCurrentSemester(Long id) {
        semesterRepository.findAll().forEach(s -> {
            s.setCurrent(s.getId().equals(id));
            semesterRepository.save(s);
        });
    }

    // --- Course Assignment operations ---
    public List<CourseAssignment> getAllCourseAssignments() {
        return courseAssignmentRepository.findAll();
    }

    public List<CourseAssignment> getAssignmentsForLecturer(Lecturer lecturer) {
        return courseAssignmentRepository.findByLecturer(lecturer);
    }

    public List<CourseAssignment> getAssignmentsForLecturerInCurrentTerm(Lecturer lecturer, AcademicYear year, Semester semester) {
        return courseAssignmentRepository.findByLecturerAndAcademicYearAndSemester(lecturer, year, semester);
    }

    public CourseAssignment assignLecturerToCourse(Course course, Lecturer lecturer, AcademicYear year, Semester semester) {
        Optional<CourseAssignment> existing = courseAssignmentRepository
                .findByCourseAndAcademicYearAndSemester(course, year, semester);
        if (existing.isPresent()) {
            CourseAssignment ca = existing.get();
            ca.setLecturer(lecturer);
            return courseAssignmentRepository.save(ca);
        } else {
            CourseAssignment ca = new CourseAssignment(course, lecturer, year, semester);
            return courseAssignmentRepository.save(ca);
        }
    }

    public void removeAssignment(Long id) {
        courseAssignmentRepository.deleteById(id);
    }
}
