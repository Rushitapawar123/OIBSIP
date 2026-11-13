package com.example.online_examination_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OptionRequest(
        @NotBlank(message = "Option ka text zaroori hai")
        @Size(max = 500, message = "Option 500 characters se zyada nahi ho sakta")
        String optionText,

        boolean correct
) {}