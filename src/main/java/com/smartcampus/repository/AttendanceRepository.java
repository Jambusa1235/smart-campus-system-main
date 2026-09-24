package com.smartcampus.repository;

import com.smartcampus.model.Attendance;
import com.smartcampus.model.Course;
import com.smartcampus.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByCourseAndAttendanceDate(Course course, LocalDate attendanceDate);
    List<Attendance> findByStudentAndCourse(Student student, Course course);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudent(Student student);
    long countByStudentAndCourseAndStatus(Student student, Course course, Attendance.Status status);
    long countByStudentAndCourse(Student student, Course course);
}
