package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Role;
import com.example.online_examination_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findTop10ByRoleAndActiveTrueOrderByXpPointsDesc(Role role);
    long countByRole(Role role);
}