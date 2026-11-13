package com.example.online_examination_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendOtp(String toEmail, String otp, String purpose) {
        // DEV MODE: OTP console mein dikhega. Baad mein yahan real email bhejenge.
        log.info("=== OTP ({}) for {} : {} (10 minute valid) ===", purpose, toEmail, otp);
    }
}