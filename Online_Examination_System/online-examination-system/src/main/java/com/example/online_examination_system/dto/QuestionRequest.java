package com.example.online_examination_system.dto;

import com.example.online_examination_system.entity.Difficulty;
import com.example.online_examination_system.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record QuestionRequest(
        @NotNull(message = "Subject chuniye")
        Long subjectId,

        @NotBlank(message = "Question ka text zaroori hai")
        String questionText,

        @NotNull(message = "Question type chuniye")
        QuestionType questionType,

        @NotNull(message = "Difficulty chuniye")
        Difficulty difficulty,

        @NotNull(message = "Marks zaroori hain")
        @DecimalMin(value = "0.25", message = "Marks kam se kam 0.25 hone chahiye")
        @DecimalMax(value = "100", message = "Marks 100 se zyada nahi ho sakte")
        BigDecimal marks,

        @Size(max = 100, message = "Topic 100 characters se zyada nahi ho sakta")
        String topic,

        String explanation,

        @NotNull(message = "Options zaroori hain")
        @Size(min = 2, max = 6, message = "Options 2 se 6 ke beech hone chahiye")
        @Valid
        List<OptionRequest> options
) {}