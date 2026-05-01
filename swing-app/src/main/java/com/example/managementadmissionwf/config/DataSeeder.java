package com.example.managementadmissionwf.config;

import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed 100 users vào database nếu bảng users trống.
 * Chạy tự động khi ứng dụng khởi động.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String SEED_PASSWORD = "admin123";
    private static final String[] FIRST_NAMES = {
            "Văn", "Thị", "Minh", "Đức", "Thanh", "Hữu", "Xuân", "Ngọc",
            "Quốc", "Thu", "Hải", "Đình", "Phúc", "Bảo", "Công", "Lê",
            "Kim", "Hoàng", "Trọng", "Vũ", "Duy", "Phương", "Anh", "Tuấn",
            "Hà", "Gia", "Trâm", "Quang", "Sơn", "Long", "Phong", "Khánh"
    };

    private static final String[] LAST_NAMES = {
            "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ",
            "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý"
    };

    private static final String[] MIDDLE_NAMES = {
            "", "", "", "An", "Bình", "Chi", "Dũng", "Em", "Phúc", "Giang",
            "Hiếu", "Khôi", "Lam", "Mai", "Nam", "Oanh", "Phong", "Quân",
            "Sang", "Tâm", "Uyên", "Vinh", "Yến", "Zoe"
    };

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already has data, skipping seed.");
            return;
        }

        log.info("Seeding 100 users...");
        List<Users> users = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            Users user = Users.builder()
                    .fullname(generateVietnameseName())
                    .email(String.format("user%03d@example.com", i))
                    .username(String.format("user%03d", i))
                    .password(passwordEncoder.encode(SEED_PASSWORD))
                    .role(i % 10 == 0 ? RoleUser.ADMIN : RoleUser.STUDENT)
                    .isDeleted(false)
                    .createdAt(LocalDate.now().minusDays((long) (Math.random() * 30)))
                    .updatedAt(LocalDate.now())
                    .build();
            users.add(user);
        }

        userRepository.saveAll(users);
        log.info("Seeded {} users successfully (90 STUDENT + 10 ADMIN). Password: password123", users.size());
    }

    private String generateVietnameseName() {
        String lastName = LAST_NAMES[(int) (Math.random() * LAST_NAMES.length)];
        String middleName = MIDDLE_NAMES[(int) (Math.random() * MIDDLE_NAMES.length)];
        String firstName = FIRST_NAMES[(int) (Math.random() * FIRST_NAMES.length)];

        return middleName.isEmpty()
                ? lastName + " " + firstName
                : lastName + " " + middleName + " " + firstName;
    }
}
 