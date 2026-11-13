package com.example.online_examination_system.config;

import com.example.online_examination_system.entity.Badge;
import com.example.online_examination_system.entity.Role;
import com.example.online_examination_system.entity.User;
import com.example.online_examination_system.repository.BadgeRepository;
import com.example.online_examination_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        createAdminIfMissing();
        createBadgesIfMissing();
    }

    private void createAdminIfMissing() {
        String email = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User admin = User.builder()
                .fullName("System Admin")
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .emailVerified(true)
                .build();
        userRepository.save(admin);
        log.info("Default admin ban gaya: {}", email);
    }

    private void createBadgesIfMissing() {
        List<Badge> badges = List.of(
                Badge.builder().code("FIRST_EXAM").name("First Step")
                        .description("Pehla exam complete kiya").icon("rocket").build(),
                Badge.builder().code("PERFECT_SCORE").name("Perfectionist")
                        .description("Exam mein 100% score kiya").icon("trophy").build(),
                Badge.builder().code("STREAK_7").name("7 Day Streak")
                        .description("Lagatar 7 din login kiya").icon("fire").build(),
                Badge.builder().code("TOP_3").name("Top 3")
                        .description("Leaderboard mein top 3 mein aaye").icon("medal").build(),
                Badge.builder().code("CLEAN_RUN").name("Honest Player")
                        .description("Bina kisi violation ke exam diya").icon("shield").build(),
                Badge.builder().code("LEVEL_5").name("Level 5")
                        .description("Level 5 tak pahunche").icon("star").build()
        );
        for (Badge b : badges) {
            if (badgeRepository.findByCode(b.getCode()).isEmpty()) {
                badgeRepository.save(b);
            }
        }
    }
}