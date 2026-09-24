package com.smartcampus.repository;

import com.smartcampus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
    List<CourseAssignment> findByLecturer(Lecturer lecturer);
    List<CourseAssignment> findByLecturerAndAcademicYearAndSemester(Lecturer lecturer, AcademicYear academicYear, Semester semester);
    List<CourseAssignment> findByAcademicYearAndSemester(AcademicYear academicYear, Semester semester);
    Optional<CourseAssignment> findByCourseAndAcademicYearAndSemester(Course course, AcademicYear academicYear, Semester semester);
    boolean existsByCourseAndAcademicYearAndSemester(Course course, AcademicYear academicYear, Semester semester);
}
