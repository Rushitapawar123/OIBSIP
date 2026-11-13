package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findByAttempt_IdOrderByOccurredAtAsc(Long attemptId);
    long countByAttempt_Id(Long attemptId);
}