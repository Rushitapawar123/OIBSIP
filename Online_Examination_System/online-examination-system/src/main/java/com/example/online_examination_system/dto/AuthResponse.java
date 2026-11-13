package com.example.online_examination_system.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String fullName,
        String email,
        String role,
        Integer xpPoints,
        Integer levelNo
) {}