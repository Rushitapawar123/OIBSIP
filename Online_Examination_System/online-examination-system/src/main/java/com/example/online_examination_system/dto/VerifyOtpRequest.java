package com.example.online_examination_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        @Pattern(regexp = "^\\d{6}$", message = "OTP 6 digit ka hona chahiye")
        String otp
) {}