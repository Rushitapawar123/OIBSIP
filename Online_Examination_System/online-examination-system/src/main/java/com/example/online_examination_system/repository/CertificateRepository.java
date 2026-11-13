package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateCode(String certificateCode);   // QR verify
    Optional<Certificate> findByAttempt_Id(Long attemptId);
    boolean existsByAttempt_Id(Long attemptId);
    List<Certificate> findByAttempt_Student_IdOrderByIssuedAtDesc(Long studentId);
}