package com.smartcampus.repository;

import com.smartcampus.model.AcademicYear;
import com.smartcampus.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByAcademicYear(AcademicYear academicYear);
    Optional<Semester> findByIsCurrentTrue();
    Optional<Semester> findByNameAndAcademicYear(String name, AcademicYear academicYear);
}
