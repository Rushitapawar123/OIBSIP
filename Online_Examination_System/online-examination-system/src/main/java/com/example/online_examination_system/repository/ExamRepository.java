package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Exam;
import com.example.online_examination_system.entity.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByCreatedBy_IdOrderByCreatedAtDesc(Long teacherId);

    List<Exam> findByStatusOrderByStartTimeAsc(ExamStatus status);

    // Student ko dikhne wale exam: published aur abhi khatam nahi hue
    List<Exam> findByStatusAndEndTimeAfterOrderByStartTimeAsc(ExamStatus status, LocalDateTime now);

    long countByStatus(ExamStatus status);
}