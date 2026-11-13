package com.example.online_examination_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Naam zaroori hai")
        @Size(min = 2, max = 100, message = "Naam 2 se 100 characters ka hona chahiye")
        String fullName,

        @NotBlank(message = "Email zaroori hai")
        @Email(message = "Email sahi format mein likhiye")
        String email,

        @NotBlank(message = "Password zaroori hai")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,64}$",
                message = "Password 8 se 64 characters ka ho, aur usme letter aur number dono hon")
        String password
) {}
