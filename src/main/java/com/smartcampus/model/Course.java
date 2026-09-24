package com.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 20)
    private String code; // e.g. "CS101"

    @NotBlank
    @Column(nullable = false, length = 150)
    private String title; // e.g. "Data Structures and Algorithms"

    @Column(nullable = false)
    private int creditUnits = 3; // Credit Units / Credit Hours

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    private int semesterOffered = 1; // 1 or 2

    private int yearLevel = 1; // 1, 2, 3, 4

    @Column(length = 500)
    private String description;

    public Course() {
    }

    public Course(String code, String title, int creditUnits, Department department, Program program, int semesterOffered, int yearLevel, String description) {
        this.code = code;
        this.title = title;
        this.creditUnits = creditUnits;
        this.department = department;
        this.program = program;
        this.semesterOffered = semesterOffered;
        this.yearLevel = yearLevel;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCreditUnits() {
        return creditUnits;
    }

    public void setCreditUnits(int creditUnits) {
        this.creditUnits = creditUnits;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public int getSemesterOffered() {
        return semesterOffered;
    }

    public void setSemesterOffered(int semesterOffered) {
        this.semesterOffered = semesterOffered;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
