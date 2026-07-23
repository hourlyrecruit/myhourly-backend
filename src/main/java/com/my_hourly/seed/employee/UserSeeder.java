package com.my_hourly.seed.employee;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserStatus;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (userRepository.count() > 3) { // HR, Manager, Super Admin created in DataInitializer
            log.info("Users already seeded. Skipping...");
            return;
        }

        List<Map<String, String>> records = csvReader.readCsv("seed/users.csv");
        for (Map<String, String> record : records) {
            String username = record.get("username");
            if (!userRepository.existsByUsername(username)) {
                User user = User.builder()
                        .username(username)
                        .email(record.get("email"))
                        .password(passwordEncoder.encode(record.get("password")))
                        .role(RoleName.valueOf(record.get("role")))
                        .userStatus(UserStatus.valueOf(record.get("user_status")))
                        .build();
                userRepository.save(user);
                log.info("Seeded user: {}", username);
            }
        }
    }
}

