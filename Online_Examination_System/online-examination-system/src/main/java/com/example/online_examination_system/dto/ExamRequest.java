package com.example.online_examination_system.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExamRequest(
        @NotBlank(message = "Exam title is required")
        @Size(min = 3, max = 150, message = "Title must be 3 to 150 characters")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Please choose a subject")
        Long subjectId,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 600, message = "Duration cannot exceed 600 minutes")
        Integer durationMinutes,

        @NotNull(message = "Pass marks are required")
        @DecimalMin(value = "0", message = "Pass marks cannot be negative")
        BigDecimal passMarks,

        @NotNull(message = "Negative marking value is required (use 0 for none)")
        @DecimalMin(value = "0", message = "Negative marking cannot be below 0")
        @DecimalMax(value = "10", message = "Negative marking cannot exceed 10")
        BigDecimal negativeMarking,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime,

        boolean shuffleQuestions,
        boolean shuffleOptions,
        boolean adaptive,
        boolean proctoringEnabled,
        boolean webcamRequired,
        boolean certificateEnabled,

        @NotNull(message = "Max violations is required")
        @Min(value = 1, message = "Max violations must be at least 1")
        @Max(value = 20, message = "Max violations cannot exceed 20")
        Integer maxViolations,

        // Questions to include (can be empty while the exam is a draft)
        List<Long> questionIds
) {}