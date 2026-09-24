package com.smartcampus.repository;

import com.smartcampus.model.Course;
import com.smartcampus.model.Department;
import com.smartcampus.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    List<Course> findByDepartment(Department department);
    List<Course> findByProgram(Program program);
    List<Course> findByProgramAndYearLevelAndSemesterOffered(Program program, int yearLevel, int semesterOffered);
    List<Course> findBySemesterOffered(int semesterOffered);
}
