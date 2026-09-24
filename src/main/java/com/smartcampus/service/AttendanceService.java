package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance recordAttendance(Course course, Student student, LocalDate date,
                                       Attendance.Status status, String topic, Lecturer lecturer) {
        Attendance attendance = new Attendance(course, student, date, status, topic, lecturer);
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceForCourseAndDate(Course course, LocalDate date) {
        return attendanceRepository.findByCourseAndAttendanceDate(course, date);
    }

    public List<Attendance> getAttendanceForStudentAndCourse(Student student, Course course) {
        return attendanceRepository.findByStudentAndCourse(student, course);
    }

    public List<Attendance> getAttendanceForStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceForCourse(Course course) {
        return attendanceRepository.findByCourse(course);
    }

    public double calculateAttendancePercentage(Student student, Course course) {
        long totalSessions = attendanceRepository.countByStudentAndCourse(student, course);
        if (totalSessions == 0) {
            return 100.0; // Default when no sessions recorded yet
        }
        long attended = attendanceRepository.countByStudentAndCourseAndStatus(student, course, Attendance.Status.PRESENT)
                + attendanceRepository.countByStudentAndCourseAndStatus(student, course, Attendance.Status.LATE)
                + attendanceRepository.countByStudentAndCourseAndStatus(student, course, Attendance.Status.EXCUSED);

        return Math.round(((double) attended / totalSessions) * 1000.0) / 10.0;
    }

    public boolean isEligibleForExam(Student student, Course course) {
        return calculateAttendancePercentage(student, course) >= 75.0;
    }
}
