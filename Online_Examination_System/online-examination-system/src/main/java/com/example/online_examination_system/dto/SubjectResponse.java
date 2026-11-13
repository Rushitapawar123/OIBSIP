package com.example.online_examination_system.dto;

public record SubjectResponse(
        Long id,
        String name,
        String description,
        long questionCount
) {}