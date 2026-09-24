package com.smartcampus.repository;

import com.smartcampus.model.Program;
import com.smartcampus.model.Student;
import com.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT s FROM Student s WHERE s.user = :user")
    Optional<Student> findByUser(@Param("user") User user);

    @Query("SELECT s FROM Student s WHERE s.user.username = :username OR s.registrationNumber = :username")
    Optional<Student> findByUserUsername(@Param("username") String username);
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Student> findByProgram(Program program);
    List<Student> findByStatus(String status);
}
