package com.smartcampus.repository;

import com.smartcampus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByCourseRegistration(CourseRegistration courseRegistration);
    Optional<Grade> findByCourseRegistrationId(Long courseRegistrationId);

    @Query("SELECT g FROM Grade g WHERE g.courseRegistration.student = :student")
    List<Grade> findByStudent(@Param("student") Student student);

    @Query("SELECT g FROM Grade g WHERE g.courseRegistration.student = :student AND g.courseRegistration.academicYear = :academicYear AND g.courseRegistration.semester = :semester")
    List<Grade> findByStudentAndAcademicYearAndSemester(
            @Param("student") Student student,
            @Param("academicYear") AcademicYear academicYear,
            @Param("semester") Semester semester);

    @Query("SELECT g FROM Grade g WHERE g.courseRegistration.course = :course AND g.courseRegistration.academicYear = :academicYear AND g.courseRegistration.semester = :semester")
    List<Grade> findByCourseAndAcademicYearAndSemester(
            @Param("course") Course course,
            @Param("academicYear") AcademicYear academicYear,
            @Param("semester") Semester semester);
}
