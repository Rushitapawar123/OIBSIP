package com.example.online_examination_system.service;

import com.example.online_examination_system.dto.*;
import com.example.online_examination_system.entity.Role;
import com.example.online_examination_system.entity.User;
import com.example.online_examination_system.repository.UserRepository;
import com.example.online_examination_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int OTP_VALID_MINUTES = 10;
    private static final int DAILY_LOGIN_XP = 5;
    private static final int XP_PER_LEVEL = 100;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    // ---------- REGISTER ----------
    @Transactional
    public String register(RegisterRequest req) {
        String email = normalize(req.email());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ye email pehle se registered hai");
        }

        String otp = generateOtp();

        User user = User.builder()
                .fullName(req.fullName().trim())
                .email(email)
                .password(passwordEncoder.encode(req.password()))
                .role(Role.STUDENT)          // register se hamesha STUDENT
                .otpCode(otp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES))
                .build();

        userRepository.save(user);
        emailService.sendOtp(email, otp, "REGISTER");
        return "Registration ho gaya. Email par bheja gaya OTP verify kijiye.";
    }

    // ---------- VERIFY EMAIL OTP ----------
    @Transactional
    public String verifyOtp(VerifyOtpRequest req) {
        User user = findByEmailOrThrow(normalize(req.email()));
        checkOtp(user, req.otp());

        user.setEmailVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
        return "Email verify ho gaya. Ab login kar sakti hain.";
    }

    // ---------- LOGIN ----------
    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(normalize(req.email()))
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aapka account band hai");
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pehle email OTP verify kijiye");
        }

        // Single-device login: naya session ID, purana token ab invalid ho jayega
        String sessionId = UUID.randomUUID().toString();
        user.setActiveSessionId(sessionId);

        updateStreakAndXp(user);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name(), sessionId);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getXpPoints(),
                user.getLevelNo());
    }

    // ---------- FORGOT PASSWORD ----------
    @Transactional
    public String forgotPassword(ForgotPasswordRequest req) {
        // Email exist kare ya na kare, jawab ek hi (kisi ko pata na chale ki account hai ya nahi)
        userRepository.findByEmail(normalize(req.email())).ifPresent(user -> {
            String otp = generateOtp();
            user.setOtpCode(otp);
            user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES));
            userRepository.save(user);
            emailService.sendOtp(user.getEmail(), otp, "RESET_PASSWORD");
        });
        return "Agar ye email registered hai to OTP bhej diya gaya hai.";
    }

    // ---------- RESET PASSWORD ----------
    @Transactional
    public String resetPassword(ResetPasswordRequest req) {
        User user = findByEmailOrThrow(normalize(req.email()));
        checkOtp(user, req.otp());

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        user.setActiveSessionId(null);   // saare purane login band
        userRepository.save(user);
        return "Password badal gaya. Ab naye password se login kijiye.";
    }

    // ---------- LOGOUT ----------
    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setActiveSessionId(null);
            userRepository.save(user);
        });
    }

    // ================= helper methods =================

    private void updateStreakAndXp(User user) {
        LocalDate today = LocalDate.now();
        LocalDate last = user.getLastActiveDate();

        if (last == null || last.isBefore(today.minusDays(1))) {
            user.setStreakDays(1);                       // streak toot gayi ya pehli baar
        } else if (last.equals(today.minusDays(1))) {
            user.setStreakDays(user.getStreakDays() + 1); // kal bhi aayi thi
        }
        // last == today: kuch nahi badalna

        if (last == null || !last.equals(today)) {
            user.setXpPoints(user.getXpPoints() + DAILY_LOGIN_XP);   // din ka pehla login bonus
            user.setLevelNo(user.getXpPoints() / XP_PER_LEVEL + 1);
        }
        user.setLastActiveDate(today);
    }

    private void checkOtp(User user, String otp) {
        if (user.getOtpCode() == null || user.getOtpExpiresAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pehle OTP mangwaiye");
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expire ho gaya, dobara mangwaiye");
        }
        if (!user.getOtpCode().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP galat hai");
        }
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User nahi mila"));
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ya password galat hai");
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}