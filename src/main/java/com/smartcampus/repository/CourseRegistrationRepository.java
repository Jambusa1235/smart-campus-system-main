package com.smartcampus.repository;

import com.smartcampus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    List<CourseRegistration> findByStudent(Student student);
    List<CourseRegistration> findByStudentAndAcademicYearAndSemester(Student student, AcademicYear academicYear, Semester semester);
    List<CourseRegistration> findByCourseAndAcademicYearAndSemester(Course course, AcademicYear academicYear, Semester semester);
    List<CourseRegistration> findByCourse(Course course);
    Optional<CourseRegistration> findByStudentAndCourseAndAcademicYearAndSemester(Student student, Course course, AcademicYear academicYear, Semester semester);
    boolean existsByStudentAndCourseAndAcademicYearAndSemester(Student student, Course course, AcademicYear academicYear, Semester semester);
    long countByCourseAndAcademicYearAndSemester(Course course, AcademicYear academicYear, Semester semester);
}
