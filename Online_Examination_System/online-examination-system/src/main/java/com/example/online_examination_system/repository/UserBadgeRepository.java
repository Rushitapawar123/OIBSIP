package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser_Id(Long userId);
    boolean existsByUser_IdAndBadge_Id(Long userId, Long badgeId);
}