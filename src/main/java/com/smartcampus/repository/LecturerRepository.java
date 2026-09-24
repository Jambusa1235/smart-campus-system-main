package com.smartcampus.repository;

import com.smartcampus.model.Department;
import com.smartcampus.model.Lecturer;
import com.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByStaffNumber(String staffNumber);
    @Query("SELECT l FROM Lecturer l WHERE l.user.username = :username OR l.staffNumber = :username")
    Optional<Lecturer> findByUserUsername(@Param("username") String username);

    @Query("SELECT l FROM Lecturer l WHERE l.user = :user")
    Optional<Lecturer> findByUser(@Param("user") User user);

    boolean existsByStaffNumber(String staffNumber);
    List<Lecturer> findByDepartment(Department department);
}
