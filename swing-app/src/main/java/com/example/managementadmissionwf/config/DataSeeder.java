package com.example.managementadmissionwf.config;

import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import com.example.managementadmissionwf.dal.repository.ConversionTableRepository;
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
 * Seed data vào database khi ứng dụng khởi động.
 * Chạy tự động nếu bảng còn trống.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversionTableRepository conversionTableRepository;
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
        seedUsersIfEmpty();
        seedConversionTablesIfEmpty();
    }

    private void seedUsersIfEmpty() {
        if (userRepository.count() > 0) {
            log.info("Users table already has data, skipping seed.");
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
        log.info("Seeded {} users successfully (90 STUDENT + 10 ADMIN). Password: {}", users.size(), SEED_PASSWORD);
    }

    private void seedConversionTablesIfEmpty() {
        if (conversionTableRepository.count() > 0) {
            log.info("Conversion tables already have data, skipping seed.");
            return;
        }

        log.info("Seeding conversion tables...");
        List<XtBangquydoi> records = new ArrayList<>();

        // VSAT data từ converted.md (8 môn × 12 khoảng = 96 records)
        records.addAll(seedVsatData());

        // THPT data (8 môn × 12 khoảng = 96 records)
        records.addAll(seedThptData());

        // DGNL data (12 khoảng)
        records.addAll(seedDgnlData());

        // IELTS data (9 bands)
        records.addAll(seedIeltsData());

        // TOEIC data (10 khoảng)
        records.addAll(seedToeicData());

        conversionTableRepository.saveAll(records);
        log.info("Seeded {} conversion table records successfully.", records.size());
    }

    private List<XtBangquydoi> seedVsatData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // TOÁN
        records.add(createRecord("VSAT", "TO", 132.0, 150.0, 8.5, 10.0, now));
        records.add(createRecord("VSAT", "TO", 128.5, 132.0, 8.1, 8.5, now));
        records.add(createRecord("VSAT", "TO", 122.5, 128.5, 7.75, 8.1, now));
        records.add(createRecord("VSAT", "TO", 114.5, 122.5, 7.0, 7.75, now));
        records.add(createRecord("VSAT", "TO", 108.0, 114.5, 6.6, 7.0, now));
        records.add(createRecord("VSAT", "TO", 102.5, 108.0, 6.25, 6.6, now));
        records.add(createRecord("VSAT", "TO", 97.0, 102.5, 6.0, 6.25, now));
        records.add(createRecord("VSAT", "TO", 91.0, 97.0, 5.6, 6.0, now));
        records.add(createRecord("VSAT", "TO", 85.0, 91.0, 5.25, 5.6, now));
        records.add(createRecord("VSAT", "TO", 77.0, 85.0, 5.0, 5.25, now));
        records.add(createRecord("VSAT", "TO", 68.0, 77.0, 4.5, 5.0, now));
        records.add(createRecord("VSAT", "TO", 6.0, 68.0, 1.5, 4.5, now));

        // VẬT LÍ
        records.add(createRecord("VSAT", "LY", 123.0, 147.0, 9.5, 10.0, now));
        records.add(createRecord("VSAT", "LY", 118.5, 123.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "LY", 112.5, 118.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "LY", 105.0, 112.5, 8.5, 9.0, now));
        records.add(createRecord("VSAT", "LY", 99.5, 105.0, 8.0, 8.5, now));
        records.add(createRecord("VSAT", "LY", 94.5, 99.5, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "LY", 90.0, 94.5, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "LY", 85.0, 90.0, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "LY", 80.0, 85.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "LY", 74.0, 80.0, 6.35, 6.75, now));
        records.add(createRecord("VSAT", "LY", 66.5, 74.0, 5.75, 6.35, now));
        records.add(createRecord("VSAT", "LY", 17.0, 66.5, 3.05, 5.75, now));

        // HÓA HỌC
        records.add(createRecord("VSAT", "HH", 129.0, 150.0, 9.5, 10.0, now));
        records.add(createRecord("VSAT", "HH", 124.5, 129.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "HH", 117.0, 124.5, 8.75, 9.25, now));
        records.add(createRecord("VSAT", "HH", 107.5, 117.0, 8.25, 8.75, now));
        records.add(createRecord("VSAT", "HH", 100.5, 107.5, 7.75, 8.25, now));
        records.add(createRecord("VSAT", "HH", 94.0, 100.5, 7.25, 7.75, now));
        records.add(createRecord("VSAT", "HH", 88.0, 94.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "HH", 81.5, 88.0, 6.25, 6.75, now));
        records.add(createRecord("VSAT", "HH", 75.5, 81.5, 5.75, 6.25, now));
        records.add(createRecord("VSAT", "HH", 68.5, 75.5, 5.25, 5.75, now));
        records.add(createRecord("VSAT", "HH", 59.5, 68.5, 4.6, 5.25, now));
        records.add(createRecord("VSAT", "HH", 20.0, 59.5, 1.35, 4.6, now));

        // SINH HỌC
        records.add(createRecord("VSAT", "SH", 130.5, 150.0, 9.0, 9.75, now));
        records.add(createRecord("VSAT", "SH", 126.5, 130.5, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "SH", 120.5, 126.5, 8.34, 8.75, now));
        records.add(createRecord("VSAT", "SH", 112.5, 120.5, 7.85, 8.34, now));
        records.add(createRecord("VSAT", "SH", 105.5, 112.5, 7.5, 7.85, now));
        records.add(createRecord("VSAT", "SH", 100.0, 105.5, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "SH", 94.5, 100.0, 6.85, 7.25, now));
        records.add(createRecord("VSAT", "SH", 88.5, 94.5, 6.5, 6.85, now));
        records.add(createRecord("VSAT", "SH", 82.5, 88.5, 6.25, 6.5, now));
        records.add(createRecord("VSAT", "SH", 76.0, 82.5, 5.85, 6.25, now));
        records.add(createRecord("VSAT", "SH", 66.5, 76.0, 5.25, 5.85, now));
        records.add(createRecord("VSAT", "SH", 26.5, 66.5, 2.8, 5.25, now));

        // LỊCH SỬ
        records.add(createRecord("VSAT", "LS", 133.5, 150.0, 9.75, 10.0, now));
        records.add(createRecord("VSAT", "LS", 131.0, 133.5, 9.5, 9.75, now));
        records.add(createRecord("VSAT", "LS", 126.5, 131.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "LS", 120.5, 126.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "LS", 115.0, 120.5, 8.5, 9.0, now));
        records.add(createRecord("VSAT", "LS", 110.0, 115.0, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "LS", 105.5, 110.0, 8.0, 8.25, now));
        records.add(createRecord("VSAT", "LS", 101.0, 105.5, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "LS", 95.5, 101.0, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "LS", 88.5, 95.5, 7.0, 7.5, now));
        records.add(createRecord("VSAT", "LS", 79.5, 88.5, 6.35, 7.0, now));
        records.add(createRecord("VSAT", "LS", 36.5, 79.5, 2.95, 6.35, now));

        // ĐỊA LÍ
        records.add(createRecord("VSAT", "DL", 124.0, 141.0, 10.0, 10.0, now));
        records.add(createRecord("VSAT", "DL", 120.5, 124.0, 10.0, 10.0, now));
        records.add(createRecord("VSAT", "DL", 115.5, 120.5, 9.75, 10.0, now));
        records.add(createRecord("VSAT", "DL", 108.5, 115.5, 9.25, 9.75, now));
        records.add(createRecord("VSAT", "DL", 103.0, 108.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "DL", 98.5, 103.0, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "DL", 94.0, 98.5, 8.5, 8.75, now));
        records.add(createRecord("VSAT", "DL", 89.5, 94.0, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "DL", 84.5, 89.5, 7.75, 8.25, now));
        records.add(createRecord("VSAT", "DL", 79.0, 84.5, 7.25, 7.75, now));
        records.add(createRecord("VSAT", "DL", 71.0, 79.0, 6.5, 7.25, now));
        records.add(createRecord("VSAT", "DL", 31.0, 71.0, 3.0, 6.5, now));

        // TIẾNG ANH
        records.add(createRecord("VSAT", "AN", 131.0, 150.0, 7.75, 9.75, now));
        records.add(createRecord("VSAT", "AN", 127.5, 131.0, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "AN", 120.5, 127.5, 7.0, 7.5, now));
        records.add(createRecord("VSAT", "AN", 112.0, 120.5, 6.5, 7.0, now));
        records.add(createRecord("VSAT", "AN", 105.0, 112.0, 6.0, 6.5, now));
        records.add(createRecord("VSAT", "AN", 98.5, 105.0, 5.75, 6.0, now));
        records.add(createRecord("VSAT", "AN", 92.0, 98.5, 5.5, 5.75, now));
        records.add(createRecord("VSAT", "AN", 85.5, 92.0, 5.25, 5.5, now));
        records.add(createRecord("VSAT", "AN", 78.5, 85.5, 5.0, 5.25, now));
        records.add(createRecord("VSAT", "AN", 70.5, 78.5, 4.5, 5.0, now));
        records.add(createRecord("VSAT", "AN", 60.0, 70.5, 4.0, 4.5, now));
        records.add(createRecord("VSAT", "AN", 20.5, 60.0, 1.25, 4.0, now));

        // NGỮ VĂN
        records.add(createRecord("VSAT", "NV", 129.5, 146.0, 9.25, 9.75, now));
        records.add(createRecord("VSAT", "NV", 127.5, 129.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "NV", 124.0, 127.5, 9.0, 9.0, now));
        records.add(createRecord("VSAT", "NV", 119.5, 124.0, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "NV", 115.5, 119.5, 8.5, 8.75, now));
        records.add(createRecord("VSAT", "NV", 112.5, 115.5, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "NV", 109.0, 112.5, 8.0, 8.25, now));
        records.add(createRecord("VSAT", "NV", 106.0, 109.0, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "NV", 102.0, 106.0, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "NV", 97.0, 102.0, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "NV", 90.0, 97.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "NV", 5.0, 90.0, 3.5, 6.75, now));

        return records;
    }

    private List<XtBangquydoi> seedThptData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // THPT có cùng cấu trúc với VSAT, chỉ khác phương thức
        String[] subjects = { "TO", "LY", "HH", "SH", "LS", "DL", "AN", "NV" };
        double[][] data = {
                { 132.0, 150.0, 8.5, 10.0 }, { 128.5, 132.0, 8.1, 8.5 }, { 122.5, 128.5, 7.75, 8.1 },
                { 114.5, 122.5, 7.0, 7.75 }, { 108.0, 114.5, 6.6, 7.0 }, { 102.5, 108.0, 6.25, 6.6 },
                { 97.0, 102.5, 6.0, 6.25 }, { 91.0, 97.0, 5.6, 6.0 }, { 85.0, 91.0, 5.25, 5.6 },
                { 77.0, 85.0, 5.0, 5.25 }, { 68.0, 77.0, 4.5, 5.0 }, { 6.0, 68.0, 1.5, 4.5 }
        };

        for (String mon : subjects) {
            for (double[] row : data) {
                records.add(createRecord("THPT", mon, row[0], row[1], row[2], row[3], now));
            }
        }

        return records;
    }

    private List<XtBangquydoi> seedDgnlData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // ĐGNL: 12 khoảng điểm tương đương
        double[][] data = {
                { 120.0, 140.0, 9.0, 10.0 }, { 110.0, 120.0, 8.5, 9.0 }, { 100.0, 110.0, 8.0, 8.5 },
                { 90.0, 100.0, 7.5, 8.0 }, { 80.0, 90.0, 7.0, 7.5 }, { 70.0, 80.0, 6.5, 7.0 },
                { 60.0, 70.0, 6.0, 6.5 }, { 50.0, 60.0, 5.5, 6.0 }, { 40.0, 50.0, 5.0, 5.5 },
                { 30.0, 40.0, 4.5, 5.0 }, { 20.0, 30.0, 4.0, 4.5 }, { 0.0, 20.0, 1.0, 4.0 }
        };

        for (double[] row : data) {
            records.add(createRecord("DGNL", "DGNL", row[0], row[1], row[2], row[3], now));
        }

        return records;
    }

    private List<XtBangquydoi> seedIeltsData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // IELTS: 9 bands
        double[][] data = {
                { 8.5, 9.0, 8.0, 9.0 }, { 8.0, 8.5, 7.5, 8.0 }, { 7.5, 8.0, 7.0, 7.5 },
                { 7.0, 7.5, 6.5, 7.0 }, { 6.5, 7.0, 6.0, 6.5 }, { 6.0, 6.5, 5.5, 6.0 },
                { 5.5, 6.0, 5.0, 5.5 }, { 5.0, 5.5, 4.5, 5.0 }, { 4.0, 5.0, 3.5, 4.5 }
        };

        for (double[] row : data) {
            records.add(createRecordNoToHop("IELTS", "IELTS", row[0], row[1], row[2], row[3], now));
        }

        return records;
    }

    private List<XtBangquydoi> seedToeicData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // TOEIC: 10 khoảng điểm
        double[][] data = {
                { 900.0, 990.0, 8.5, 10.0 }, { 850.0, 900.0, 8.0, 8.5 }, { 800.0, 850.0, 7.5, 8.0 },
                { 750.0, 800.0, 7.0, 7.5 }, { 700.0, 750.0, 6.5, 7.0 }, { 650.0, 700.0, 6.0, 6.5 },
                { 600.0, 650.0, 5.5, 6.0 }, { 550.0, 600.0, 5.0, 5.5 }, { 500.0, 550.0, 4.5, 5.0 },
                { 0.0, 500.0, 1.0, 4.5 }
        };

        for (double[] row : data) {
            records.add(createRecordNoToHop("TOEIC", "TOEIC", row[0], row[1], row[2], row[3], now));
        }

        return records;
    }

    private XtBangquydoi createRecord(String phuongThuc, String mon, double diemA, double diemB,
            double diemC, double diemD, LocalDate now) {
        return XtBangquydoi.builder()
                .dPhuongthuc(phuongThuc)
                .dTohop("A00") // Default tổ hợp
                .dMon(mon)
                .dDiema(diemA)
                .dDiemb(diemB)
                .dDiemc(diemC)
                .dDiemd(diemD)
                .build();
    }

    private XtBangquydoi createRecordNoToHop(String phuongThuc, String mon, double diemA, double diemB,
            double diemC, double diemD, LocalDate now) {
        return XtBangquydoi.builder()
                .dPhuongthuc(phuongThuc)
                .dTohop(null) // Không có tổ hợp
                .dMon(mon)
                .dDiema(diemA)
                .dDiemb(diemB)
                .dDiemc(diemC)
                .dDiemd(diemD)
                .build();
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