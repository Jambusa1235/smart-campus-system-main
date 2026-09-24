package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GradingService {

    private final GradeRepository gradeRepository;

    public GradingService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public Grade recordMarks(CourseRegistration registration, Double courseworkMark, Double examMark, Lecturer lecturer) {
        Optional<Grade> existing = gradeRepository.findByCourseRegistration(registration);
        Grade grade = existing.orElseGet(Grade::new);

        grade.setCourseRegistration(registration);
        grade.setCourseworkMark(courseworkMark != null ? Math.max(0, Math.min(40, courseworkMark)) : null);
        grade.setExamMark(examMark != null ? Math.max(0, Math.min(60, examMark)) : null);

        if (grade.getCourseworkMark() != null && grade.getExamMark() != null) {
            double total = grade.getCourseworkMark() + grade.getExamMark();
            grade.setTotalMark(Math.round(total * 10.0) / 10.0);

            // Calculate Grade Letter & Grade Point
            calculateGradeDetails(grade);
        } else {
            grade.setTotalMark(null);
            grade.setGradeLetter("INC");
            grade.setGradePoint(0.0);
            grade.setRemarks("Incomplete");
        }

        grade.setEnteredBy(lecturer);
        grade.setUpdatedAt(java.time.LocalDateTime.now());

        return gradeRepository.save(grade);
    }

    private void calculateGradeDetails(Grade grade) {
        double total = grade.getTotalMark();
        if (total >= 80.0) {
            grade.setGradeLetter("A");
            grade.setGradePoint(5.0);
            grade.setRemarks("Pass - Excellent");
        } else if (total >= 70.0) {
            grade.setGradeLetter("B");
            grade.setGradePoint(4.0);
            grade.setRemarks("Pass - Very Good");
        } else if (total >= 60.0) {
            grade.setGradeLetter("C");
            grade.setGradePoint(3.0);
            grade.setRemarks("Pass - Good");
        } else if (total >= 50.0) {
            grade.setGradeLetter("D");
            grade.setGradePoint(2.0);
            grade.setRemarks("Pass");
        } else {
            grade.setGradeLetter("F");
            grade.setGradePoint(0.0);
            grade.setRemarks("Fail");
        }
    }

    public Optional<Grade> getGradeForRegistration(CourseRegistration registration) {
        return gradeRepository.findByCourseRegistration(registration);
    }

    public List<Grade> getGradesForStudent(Student student) {
        return gradeRepository.findByStudent(student);
    }

    public List<Grade> getGradesForStudentInTerm(Student student, AcademicYear year, Semester semester) {
        return gradeRepository.findByStudentAndAcademicYearAndSemester(student, year, semester);
    }

    public List<Grade> getGradesForCourseInTerm(Course course, AcademicYear year, Semester semester) {
        return gradeRepository.findByCourseAndAcademicYearAndSemester(course, year, semester);
    }

    public double calculateSemesterGPA(Student student, AcademicYear year, Semester semester) {
        List<Grade> grades = getGradesForStudentInTerm(student, year, semester);
        if (grades.isEmpty()) {
            return 0.0;
        }

        double totalWeightedPoints = 0.0;
        int totalCreditUnits = 0;

        for (Grade g : grades) {
            if (g.getGradePoint() != null && g.getTotalMark() != null) {
                int cu = g.getCourseRegistration().getCourse().getCreditUnits();
                totalWeightedPoints += (g.getGradePoint() * cu);
                totalCreditUnits += cu;
            }
        }

        if (totalCreditUnits == 0) {
            return 0.0;
        }

        return Math.round((totalWeightedPoints / totalCreditUnits) * 100.0) / 100.0;
    }

    public double calculateCumulativeGPA(Student student) {
        List<Grade> grades = getGradesForStudent(student);
        if (grades.isEmpty()) {
            return 0.0;
        }

        double totalWeightedPoints = 0.0;
        int totalCreditUnits = 0;

        for (Grade g : grades) {
            if (g.getGradePoint() != null && g.getTotalMark() != null) {
                int cu = g.getCourseRegistration().getCourse().getCreditUnits();
                totalWeightedPoints += (g.getGradePoint() * cu);
                totalCreditUnits += cu;
            }
        }

        if (totalCreditUnits == 0) {
            return 0.0;
        }

        return Math.round((totalWeightedPoints / totalCreditUnits) * 100.0) / 100.0;
    }

    public String getAcademicStanding(double cgpa) {
        if (cgpa >= 4.40) return "First Class Honours (Distinction)";
        if (cgpa >= 3.60) return "Second Class Honours (Upper Division)";
        if (cgpa >= 2.80) return "Second Class Honours (Lower Division)";
        if (cgpa >= 2.00) return "Pass Degree";
        return "Academic Probation";
    }
}
