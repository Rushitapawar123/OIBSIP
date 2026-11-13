package com.example.online_examination_system.dto;

import com.example.online_examination_system.entity.Difficulty;
import com.example.online_examination_system.entity.QuestionType;

import java.math.BigDecimal;
import java.util.List;

public record QuestionResponse(
        Long id,
        Long subjectId,
        String subjectName,
        String questionText,
        QuestionType questionType,
        Difficulty difficulty,
        BigDecimal marks,
        String topic,
        String explanation,
        boolean aiGenerated,
        boolean approved,
        List<OptionResponse> options
) {}