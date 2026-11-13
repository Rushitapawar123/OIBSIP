package com.example.online_examination_system.dto;

import com.example.online_examination_system.entity.ExamStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExamResponse(
        Long id,
        String title,
        String description,
        Long subjectId,
        String subjectName,
        Integer durationMinutes,
        BigDecimal totalMarks,
        BigDecimal passMarks,
        BigDecimal negativeMarking,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean shuffleQuestions,
        boolean shuffleOptions,
        boolean adaptive,
        boolean proctoringEnabled,
        boolean webcamRequired,
        boolean certificateEnabled,
        Integer maxViolations,
        ExamStatus status,
        int questionCount,
        String createdByName,
        List<ExamQuestionSummary> questions
) {}
