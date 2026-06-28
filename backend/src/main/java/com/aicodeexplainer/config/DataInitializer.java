package com.aicodeexplainer.config;

import com.aicodeexplainer.entity.Role;
import com.aicodeexplainer.entity.User;
import com.aicodeexplainer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDefaultAdmin() {
        return args -> {
            if (!userRepository.existsByEmail("admin@aicodeexplainer.com")) {
                User admin = User.builder()
                        .name("System Admin")
                        .email("admin@aicodeexplainer.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                log.info("Default admin user created: admin@aicodeexplainer.com / admin123");
            }
        };
    }
}
