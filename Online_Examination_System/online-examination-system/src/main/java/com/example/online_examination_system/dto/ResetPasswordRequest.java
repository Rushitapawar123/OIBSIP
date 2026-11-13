package com.example.online_examination_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        @Pattern(regexp = "^\\d{6}$", message = "OTP 6 digit ka hona chahiye")
        String otp,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,64}$",
                message = "Password 8 se 64 characters ka ho, aur usme letter aur number dono hon")
        String newPassword
) {}