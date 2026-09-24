package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.AcademicYearRepository;
import com.smartcampus.repository.CourseRegistrationRepository;
import com.smartcampus.repository.SemesterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final GradingService gradingService;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;

    public ReportService(GradingService gradingService,
                         CourseRegistrationRepository courseRegistrationRepository,
                         AcademicYearRepository academicYearRepository,
                         SemesterRepository semesterRepository) {
        this.gradingService = gradingService;
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.academicYearRepository = academicYearRepository;
        this.semesterRepository = semesterRepository;
    }

    // --- Transcript Data Structure ---
        public record SemesterTranscriptItem(Semester semester, AcademicYear academicYear, List<Grade> grades, double gpa,
                                             int totalCreditUnits) {
    }

    public record StudentTranscript(Student student, List<SemesterTranscriptItem> semesterRecords, double cumulativeGPA,
                                    int totalCreditsEarned, String academicStanding) {
    }

    public record CoursePerformanceReport(Course course, AcademicYear academicYear, Semester semester,
                                          int totalStudents, double averageScore, int passCount, int failCount,
                                          double passRate, List<Grade> grades) {
    }

    public StudentTranscript generateStudentTranscript(Student student) {
        List<CourseRegistration> allRegistrations = courseRegistrationRepository.findByStudent(student);

        // Group registrations by AcademicYear and Semester
        Map<String, List<CourseRegistration>> grouped = new LinkedHashMap<>();
        for (CourseRegistration reg : allRegistrations) {
            String key = reg.getAcademicYear().getName() + " - " + reg.getSemester().getName();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(reg);
        }

        List<SemesterTranscriptItem> semesterRecords = new ArrayList<>();
        int totalCreditsEarned = 0;

        for (Map.Entry<String, List<CourseRegistration>> entry : grouped.entrySet()) {
            List<CourseRegistration> regs = entry.getValue();
            if (regs.isEmpty()) continue;

            AcademicYear year = regs.get(0).getAcademicYear();
            Semester sem = regs.get(0).getSemester();

            List<Grade> termGrades = new ArrayList<>();
            int termCredits = 0;

            for (CourseRegistration reg : regs) {
                Optional<Grade> gradeOpt = gradingService.getGradeForRegistration(reg);
                if (gradeOpt.isPresent()) {
                    Grade g = gradeOpt.get();
                    termGrades.add(g);
                    if (g.getTotalMark() != null && g.getTotalMark() >= 50.0) {
                        termCredits += reg.getCourse().getCreditUnits();
                        totalCreditsEarned += reg.getCourse().getCreditUnits();
                    }
                }
            }

            double semGPA = gradingService.calculateSemesterGPA(student, year, sem);
            semesterRecords.add(new SemesterTranscriptItem(sem, year, termGrades, semGPA, termCredits));
        }

        double cgpa = gradingService.calculateCumulativeGPA(student);
        String standing = gradingService.getAcademicStanding(cgpa);

        return new StudentTranscript(student, semesterRecords, cgpa, totalCreditsEarned, standing);
    }

    public CoursePerformanceReport generateCourseReport(Course course, AcademicYear academicYear, Semester semester) {
        List<Grade> grades = gradingService.getGradesForCourseInTerm(course, academicYear, semester);
        int total = grades.size();
        if (total == 0) {
            return new CoursePerformanceReport(course, academicYear, semester, 0, 0.0, 0, 0, 0.0, Collections.emptyList());
        }

        double sum = 0.0;
        int pass = 0;
        int fail = 0;
        int gradedCount = 0;

        for (Grade g : grades) {
            if (g.getTotalMark() != null) {
                sum += g.getTotalMark();
                gradedCount++;
                if (g.getTotalMark() >= 50.0) {
                    pass++;
                } else {
                    fail++;
                }
            }
        }

        double avg = gradedCount > 0 ? (Math.round((sum / gradedCount) * 10.0) / 10.0) : 0.0;
        double passRate = gradedCount > 0 ? (Math.round(((double) pass / gradedCount) * 1000.0) / 10.0) : 0.0;

        return new CoursePerformanceReport(course, academicYear, semester, total, avg, pass, fail, passRate, grades);
    }
}
