package com.example.online_examination_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank(message = "Subject ka naam zaroori hai")
        @Size(min = 2, max = 100, message = "Naam 2 se 100 characters ka hona chahiye")
        String name,

        @Size(max = 255, message = "Description 255 characters se zyada nahi ho sakti")
        String description
) {}