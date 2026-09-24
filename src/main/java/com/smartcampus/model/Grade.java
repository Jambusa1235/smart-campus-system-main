package com.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_registration_id", nullable = false, unique = true)
    private CourseRegistration courseRegistration;

    private Double courseworkMark; // e.g. out of 40 (or 0-100)
    private Double examMark;       // e.g. out of 60 (or 0-100)
    private Double totalMark;      // Total = courseworkMark + examMark (0 - 100)

    @Column(length = 5)
    private String gradeLetter;    // A, B, C, D, F

    private Double gradePoint;     // 5.0, 4.0, 3.0, 2.0, 0.0

    @Column(length = 30)
    private String remarks;        // Pass, Fail, Incomplete

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entered_by_lecturer_id")
    private Lecturer enteredBy;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Grade() {
    }

    public Grade(CourseRegistration courseRegistration, Double courseworkMark, Double examMark, Double totalMark, String gradeLetter, Double gradePoint, String remarks, Lecturer enteredBy) {
        this.courseRegistration = courseRegistration;
        this.courseworkMark = courseworkMark;
        this.examMark = examMark;
        this.totalMark = totalMark;
        this.gradeLetter = gradeLetter;
        this.gradePoint = gradePoint;
        this.remarks = remarks;
        this.enteredBy = enteredBy;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseRegistration getCourseRegistration() {
        return courseRegistration;
    }

    public void setCourseRegistration(CourseRegistration courseRegistration) {
        this.courseRegistration = courseRegistration;
    }

    public Double getCourseworkMark() {
        return courseworkMark;
    }

    public void setCourseworkMark(Double courseworkMark) {
        this.courseworkMark = courseworkMark;
    }

    public Double getExamMark() {
        return examMark;
    }

    public void setExamMark(Double examMark) {
        this.examMark = examMark;
    }

    public Double getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(Double totalMark) {
        this.totalMark = totalMark;
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public void setGradeLetter(String gradeLetter) {
        this.gradeLetter = gradeLetter;
    }

    public Double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(Double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Lecturer getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(Lecturer enteredBy) {
        this.enteredBy = enteredBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
