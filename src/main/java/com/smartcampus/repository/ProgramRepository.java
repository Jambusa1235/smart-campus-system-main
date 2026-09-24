package com.smartcampus.repository;

import com.smartcampus.model.Department;
import com.smartcampus.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByCode(String code);
    boolean existsByCode(String code);
    List<Program> findByDepartment(Department department);
}
