package com.example.online_examination_system.dto;

import com.example.online_examination_system.entity.Difficulty;

import java.math.BigDecimal;

public record ExamQuestionSummary(
        Long id,
        String questionText,
        Difficulty difficulty,
        BigDecimal marks,
        String topic
) {}