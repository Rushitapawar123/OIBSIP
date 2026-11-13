package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Attempt;
import com.example.online_examination_system.entity.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    Optional<Attempt> findByExam_IdAndStudent_Id(Long examId, Long studentId);

    boolean existsByExam_IdAndStudent_Id(Long examId, Long studentId);

    List<Attempt> findByStudent_IdOrderByStartedAtDesc(Long studentId);

    // Leaderboard: score zyada, aur barabari par kam time wala upar
    List<Attempt> findByExam_IdAndStatusNotOrderByScoreDescSubmittedAtAsc(Long examId, AttemptStatus status);

    // Auto-submit scheduler: time nikal chuke attempts
    List<Attempt> findByStatusAndDeadlineAtBefore(AttemptStatus status, LocalDateTime time);

    // Live monitoring: abhi exam de rahe students
    List<Attempt> findByExam_IdAndStatus(Long examId, AttemptStatus status);

    long countByExam_Id(Long examId);

    // Analytics
    @Query("select avg(a.percentage) from Attempt a where a.exam.id = :examId and a.status <> 'IN_PROGRESS'")
    Double averagePercentageByExamId(@Param("examId") Long examId);

    @Query("select count(a) from Attempt a where a.exam.id = :examId and a.status <> 'IN_PROGRESS' and a.score >= a.exam.passMarks")
    long countPassedByExamId(@Param("examId") Long examId);
}