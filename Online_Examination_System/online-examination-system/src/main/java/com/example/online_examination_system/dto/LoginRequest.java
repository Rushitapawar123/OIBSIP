package com.example.online_examination_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email zaroori hai")
        @Email(message = "Email sahi format mein likhiye")
        String email,

        @NotBlank(message = "Password zaroori hai")
        String password
) {}