package com.example.managementadmissionwf.config;

import com.example.managementadmissionwf.dal.entity.RoleUser;
import com.example.managementadmissionwf.dal.entity.Users;
import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.entity.XtTohopMonthi;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.ConversionTableRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dal.repository.SubjectGroupRepository;
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
    private final SubjectGroupRepository subjectGroupRepository;
    private final MajorRepository majorRepository;
    private final CandidateRepository candidateRepository;
    private final ScoreRepository scoreRepository;
    private final NganhTohopRepository nganhTohopRepository;
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
        seedSubjectGroupsIfEmpty();
        seedMajorsIfEmpty();
        seedMajorsSubjectGroupMappingsIfEmpty();
        seedCandidatesAndScoresIfEmpty();
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

    private void seedMajorsIfEmpty() {
        if (majorRepository.count() > 0) {
            log.info("Majors table already has data, skipping seed.");
            return;
        }

        log.info("Seeding 10 majors...");
        List<XtNganh> majors = List.of(
            // ========== KHỐI A - CÔNG NGHỆ ==========
            XtNganh.builder()
                .manganh("7480201").tennganh("Công nghệ thông tin")
                .nTohopgoc("A00").nChitieu(200)
                .nDiemsan(22.5).nDiemtrungtuyen(26.0)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(false)
                .slXtt(10).slDgnl(30).slVsat(0).slThpt(160)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7480103").tennganh("Kỹ thuật phần mềm")
                .nTohopgoc("A00").nChitieu(150)
                .nDiemsan(22.5).nDiemtrungtuyen(25.5)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(false)
                .slXtt(8).slDgnl(25).slVsat(0).slThpt(117)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7480101").tennganh("Khoa học máy tính")
                .nTohopgoc("A00").nChitieu(100)
                .nDiemsan(22.5).nDiemtrungtuyen(26.5)
                .nThpt(true).nDgnl(true).nVsat(true).nTuyenthang(false)
                .slXtt(5).slDgnl(20).slVsat(15).slThpt(60)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7480202").tennganh("An toàn thông tin")
                .nTohopgoc("A00").nChitieu(80)
                .nDiemsan(22.5).nDiemtrungtuyen(25.0)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(false)
                .slXtt(5).slDgnl(15).slVsat(0).slThpt(60)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI D - KINH TẾ ==========
            XtNganh.builder()
                .manganh("7340101").tennganh("Quản trị kinh doanh")
                .nTohopgoc("D01").nChitieu(180)
                .nDiemsan(18.0).nDiemtrungtuyen(22.0)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(true)
                .slXtt(10).slDgnl(30).slVsat(0).slThpt(140)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7340301").tennganh("Kế toán")
                .nTohopgoc("D01").nChitieu(150)
                .nDiemsan(18.0).nDiemtrungtuyen(21.5)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(true)
                .slXtt(8).slDgnl(25).slVsat(0).slThpt(117)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7340201").tennganh("Tài chính - Ngân hàng")
                .nTohopgoc("D01").nChitieu(120)
                .nDiemsan(18.0).nDiemtrungtuyen(22.5)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(false)
                .slXtt(5).slDgnl(20).slVsat(0).slThpt(95)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI C - XÃ HỘI ==========
            XtNganh.builder()
                .manganh("7380101").tennganh("Luật")
                .nTohopgoc("C00").nChitieu(100)
                .nDiemsan(18.0).nDiemtrungtuyen(20.0)
                .nThpt(true).nDgnl(true).nVsat(false).nTuyenthang(false)
                .slXtt(0).slDgnl(15).slVsat(0).slThpt(85)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtNganh.builder()
                .manganh("7140209").tennganh("Sư phạm Toán học")
                .nTohopgoc("A00").nChitieu(80)
                .nDiemsan(18.0).nDiemtrungtuyen(19.5)
                .nThpt(true).nDgnl(false).nVsat(false).nTuyenthang(true)
                .slXtt(10).slDgnl(0).slVsat(0).slThpt(70)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI B - Y DƯỢC ==========
            XtNganh.builder()
                .manganh("7210101").tennganh("Y khoa")
                .nTohopgoc("B00").nChitieu(50)
                .nDiemsan(23.0).nDiemtrungtuyen(27.0)
                .nThpt(true).nDgnl(false).nVsat(false).nTuyenthang(false)
                .slXtt(0).slDgnl(0).slVsat(0).slThpt(50)
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build()
        );

        majorRepository.saveAll(majors);
        log.info("Seeded {} majors successfully.", majors.size());
    }

    private void seedMajorsSubjectGroupMappingsIfEmpty() {
        if (nganhTohopRepository.count() > 0) {
            log.info("Major-SubjectGroup mappings already exist, skipping seed.");
            return;
        }

        log.info("Seeding major-subject group mappings...");
        
        // Mapping ngành với tổ hợp (manganh, matohop, mon1, mon2, mon3, hs1, hs2, hs3)
        // Sử dụng LI thay vì LY để match với entity
        List<XtNganhTohop> mappings = List.of(
            // CNTT - A00 (Toán Lý Hóa, hệ số 1)
            createMapping("7480201", "A00", "TO", "LI", "HO", 1.0, 1.0, 1.0),
            // CNTT - A01 (Toán Lý Anh)
            createMapping("7480201", "A01", "TO", "LI", "AN", 1.0, 1.0, 1.0),
            
            // KTPM - A00
            createMapping("7480103", "A00", "TO", "LI", "HO", 1.0, 1.0, 1.0),
            // KTPM - A01
            createMapping("7480103", "A01", "TO", "LI", "AN", 1.0, 1.0, 1.0),
            
            // KHMT - A00
            createMapping("7480101", "A00", "TO", "LI", "HO", 1.0, 1.0, 1.0),
            // KHMT - A02 (Toán Lý Sinh)
            createMapping("7480101", "A02", "TO", "LI", "SI", 1.0, 1.0, 1.0),
            
            // ATTT - A00
            createMapping("7480202", "A00", "TO", "LI", "HO", 1.0, 1.0, 1.0),
            // ATTT - A01
            createMapping("7480202", "A01", "TO", "LI", "AN", 1.0, 1.0, 1.0),
            
            // Quản trị kinh doanh - D01 (Toán Văn Anh)
            createMapping("7340101", "D01", "TO", "VA", "AN", 1.0, 1.0, 1.0),
            // QTKD - D07 (Toán Hóa Anh)
            createMapping("7340101", "D07", "TO", "HO", "AN", 1.0, 1.0, 1.0),
            
            // Kế toán - D01
            createMapping("7340301", "D01", "TO", "VA", "AN", 1.0, 1.0, 1.0),
            // Kế toán - D07
            createMapping("7340301", "D07", "TO", "HO", "AN", 1.0, 1.0, 1.0),
            
            // Tài chính Ngân hàng - D01
            createMapping("7340201", "D01", "TO", "VA", "AN", 1.0, 1.0, 1.0),
            // TCNH - D09 (Toán Sử Anh)
            createMapping("7340201", "D09", "TO", "SU", "AN", 1.0, 1.0, 1.0),
            
            // Luật - C00 (Văn Sử Địa)
            createMapping("7380101", "C00", "VA", "SU", "DI", 1.0, 1.0, 1.0),
            // Luật - C01 (Văn Sử Lý)
            createMapping("7380101", "C01", "VA", "SU", "LI", 1.0, 1.0, 1.0),
            
            // Sư phạm Toán - A00
            createMapping("7140209", "A00", "TO", "LI", "HO", 1.0, 1.0, 1.0),
            // Sư phạm Toán - A01
            createMapping("7140209", "A01", "TO", "LI", "AN", 1.0, 1.0, 1.0),
            
            // Y khoa - B00 (Toán Hóa Sinh)
            createMapping("7210101", "B00", "TO", "HO", "SI", 1.0, 1.0, 1.0)
        );
        
        nganhTohopRepository.saveAll(mappings);
        log.info("Seeded {} major-subject group mappings successfully.", mappings.size());
    }

    private XtNganhTohop createMapping(String manganh, String matohop, 
            String mon1, String mon2, String mon3, 
            double hs1, double hs2, double hs3) {
        return XtNganhTohop.builder()
            .manganh(manganh)
            .matohop(matohop)
            .thMon1(mon1)
            .thMon2(mon2)
            .thMon3(mon3)
            .hsmon1(hs1)
            .hsmon2(hs2)
            .hsmon3(hs3)
            .isDeleted(false)
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();
    }

    private void seedCandidatesAndScoresIfEmpty() {
        if (candidateRepository.count() > 0) {
            log.info("Candidates table already has data, skipping seed.");
            return;
        }

        log.info("Seeding 7 candidates with multiple score types...");
        
        // Data mẫu: 7 thí sinh với thông tin
        List<XtThisinhxettuyen25> candidates = List.of(
            // 5 thí sinh ban đầu
            XtThisinhxettuyen25.builder()
                .cccd("001082001234")
                .sobaodanh("SBD001")
                .ho("Nguyễn")
                .ten("An")
                .ngaySinh(LocalDate.of(2006, 3, 15))
                .gioiTinh("Nam")
                .noiSinh("Hà Nội")
                .khuVuc("KV1")
                .hoVaTen("Nguyễn An")
                .email("nguyen.an@example.com")
                .dienThoai("0912345678")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            XtThisinhxettuyen25.builder()
                .cccd("001082001235")
                .sobaodanh("SBD002")
                .ho("Trần")
                .ten("Bình")
                .ngaySinh(LocalDate.of(2006, 5, 22))
                .gioiTinh("Nam")
                .noiSinh("TP.HCM")
                .khuVuc("KV2")
                .hoVaTen("Trần Bình")
                .email("tran.binh@example.com")
                .dienThoai("0912345679")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            XtThisinhxettuyen25.builder()
                .cccd("001082001236")
                .sobaodanh("SBD003")
                .ho("Lê")
                .ten("Hương")
                .ngaySinh(LocalDate.of(2006, 7, 8))
                .gioiTinh("Nữ")
                .noiSinh("Đà Nẵng")
                .khuVuc("KV2")
                .hoVaTen("Lê Hương")
                .email("le.huong@example.com")
                .dienThoai("0912345680")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            XtThisinhxettuyen25.builder()
                .cccd("001082001237")
                .sobaodanh("SBD004")
                .ho("Phạm")
                .ten("Dũng")
                .ngaySinh(LocalDate.of(2006, 9, 30))
                .gioiTinh("Nam")
                .noiSinh("Cần Thơ")
                .khuVuc("KV3")
                .hoVaTen("Phạm Dũng")
                .email("pham.dung@example.com")
                .dienThoai("0912345681")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            XtThisinhxettuyen25.builder()
                .cccd("001082001238")
                .sobaodanh("SBD005")
                .ho("Hoàng")
                .ten("Lan")
                .ngaySinh(LocalDate.of(2006, 12, 25))
                .gioiTinh("Nữ")
                .noiSinh("Hải Phòng")
                .khuVuc("KV1")
                .hoVaTen("Hoàng Lan")
                .email("hoang.lan@example.com")
                .dienThoai("0912345682")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            // Thí sinh 6: Điểm DGNL
            XtThisinhxettuyen25.builder()
                .cccd("001082001239")
                .sobaodanh("SBD006")
                .ho("Vũ")
                .ten("Minh")
                .ngaySinh(LocalDate.of(2006, 4, 10))
                .gioiTinh("Nam")
                .noiSinh("Quảng Ninh")
                .khuVuc("KV2")
                .hoVaTen("Vũ Minh")
                .email("vu.minh@example.com")
                .dienThoai("0912345683")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build(),
            // Thí sinh 7: Điểm VSAT
            XtThisinhxettuyen25.builder()
                .cccd("001082001240")
                .sobaodanh("SBD007")
                .ho("Đỗ")
                .ten("Thảo")
                .ngaySinh(LocalDate.of(2006, 8, 20))
                .gioiTinh("Nữ")
                .noiSinh("Thanh Hóa")
                .khuVuc("KV3")
                .hoVaTen("Đỗ Thảo")
                .email("do.thao@example.com")
                .dienThoai("0912345684")
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build()
        );
        
        candidateRepository.saveAll(candidates);
        log.info("Seeded {} candidates successfully.", candidates.size());
        
        // Tạo điểm thi đa phương thức cho 7 thí sinh
        List<XtDiemthixettuyen> scores = new ArrayList<>();
        
        // ========== THÍ SINH 1: Nguyễn An ==========
        // Điểm THPT
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001234").sobaodanh("SBD001").dPhuongthuc("THPT")
            .to(9.5).li(9.0).ho(8.8).si(8.3).su(7.3).di(7.8).va(8.7)
            .n1Thi(7.5).n1Cc(7.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        // Điểm DGNL - NL1 thang 1200 (ví dụ: 900 điểm → quy đổi 22.5 thang 30)
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001234").sobaodanh("SBD001").dPhuongthuc("DGNL")
            .nl1(900.0).nk1(null).nk2(null) // Chỉ dùng NL1, thang 1200
            .n1Thi(8.0).n1Cc(8.0)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 2: Trần Bình ==========
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001235").sobaodanh("SBD002").dPhuongthuc("THPT")
            .to(7.3).li(7.2).ho(6.7).si(6.5).su(6.7).di(7.0).va(7.5)
            .n1Thi(6.0).n1Cc(6.0)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        // NL1 = 750 → quy đổi 18.75 thang 30
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001235").sobaodanh("SBD002").dPhuongthuc("DGNL")
            .nl1(750.0).nk1(null).nk2(null)
            .n1Thi(6.5).n1Cc(6.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 3: Lê Hương ==========
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001236").sobaodanh("SBD003").dPhuongthuc("THPT")
            .to(8.5).li(8.0).ho(8.7).si(8.5).su(7.2).di(7.3).va(8.2)
            .n1Thi(8.5).n1Cc(8.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        // NL1 = 950 → quy đổi 23.75 thang 30
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001236").sobaodanh("SBD003").dPhuongthuc("DGNL")
            .nl1(950.0).nk1(null).nk2(null)
            .n1Thi(8.5).n1Cc(8.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 4: Phạm Dũng ==========
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001237").sobaodanh("SBD004").dPhuongthuc("THPT")
            .to(6.0).li(5.8).ho(5.3).si(5.2).su(6.2).di(6.3).va(6.7)
            .n1Thi(5.0).n1Cc(5.0)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        // NL1 = 550 → quy đổi 13.75 thang 30
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001237").sobaodanh("SBD004").dPhuongthuc("DGNL")
            .nl1(550.0).nk1(null).nk2(null)
            .n1Thi(5.5).n1Cc(5.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 5: Hoàng Lan ==========
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001238").sobaodanh("SBD005").dPhuongthuc("THPT")
            .to(7.0).li(6.8).ho(7.3).si(7.2).su(6.8).di(6.5).va(7.2)
            .n1Thi(6.5).n1Cc(6.5)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        // NL1 = 800 → quy đổi 20.0 thang 30
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001238").sobaodanh("SBD005").dPhuongthuc("DGNL")
            .nl1(800.0).nk1(null).nk2(null)
            .n1Thi(7.0).n1Cc(7.0)
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 6: Vũ Minh - ĐGNL + IELTS ==========
        // Điểm DGNL - NL1 = 1100 → quy đổi 27.5 thang 30 (Xuất sắc)
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001239").sobaodanh("SBD006").dPhuongthuc("DGNL")
            .nl1(1100.0).nk1(null).nk2(null)
            .n1Thi(null).n1Cc(8.5) // IELTS 8.0 quy đổi 8.5
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        // ========== THÍ SINH 7: Đỗ Thảo - VSAT + TOEIC ==========
        // Điểm VSAT (phương thức chính)
        scores.add(XtDiemthixettuyen.builder()
            .cccd("001082001240").sobaodanh("SBD007").dPhuongthuc("VSAT")
            .to(138.5).li(135.0).ho(140.0).si(132.0)
            .su(128.0).di(125.0).va(130.0)
            .n1Thi(null).n1Cc(7.5) // TOEIC 800 quy đổi 7.5
            .isDeleted(false).createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build());
        
        scoreRepository.saveAll(scores);
        log.info("Seeded {} score records (THPT + DGNL + VSAT) successfully.", scores.size());
    }

    private void seedSubjectGroupsIfEmpty() {
        if (subjectGroupRepository.count() > 0) {
            log.info("Subject groups table already has data, skipping seed.");
            return;
        }

        log.info("Seeding 10 subject groups...");
        List<XtTohopMonthi> groups = List.of(
            // ========== KHỐI A (Khoa học tự nhiên) ==========
            XtTohopMonthi.builder()
                .matohop("A00").mon1("TO").mon2("LI").mon3("HO")
                .tentohop("Khối A00 - Toán, Lý, Hóa")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("A01").mon1("TO").mon2("LI").mon3("AN")
                .tentohop("Khối A01 - Toán, Lý, Tiếng Anh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("A02").mon1("TO").mon2("LI").mon3("SH")
                .tentohop("Khối A02 - Toán, Lý, Sinh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI B (Khoa học sự sống) ==========
            XtTohopMonthi.builder()
                .matohop("B00").mon1("TO").mon2("HO").mon3("SH")
                .tentohop("Khối B00 - Toán, Hóa, Sinh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("B01").mon1("TO").mon2("SH").mon3("DI")
                .tentohop("Khối B01 - Toán, Sinh, Địa")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI C (Xã hội) ==========
            XtTohopMonthi.builder()
                .matohop("C00").mon1("VA").mon2("SU").mon3("DI")
                .tentohop("Khối C00 - Ngữ văn, Lịch sử, Địa lý")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("C01").mon1("VA").mon2("SU").mon3("LI")
                .tentohop("Khối C01 - Ngữ văn, Lịch sử, Vật lý")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            // ========== KHỐI D (Ngoại ngữ) ==========
            XtTohopMonthi.builder()
                .matohop("D01").mon1("TO").mon2("VA").mon3("AN")
                .tentohop("Khối D01 - Toán, Ngữ văn, Tiếng Anh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("D07").mon1("TO").mon2("HO").mon3("AN")
                .tentohop("Khối D07 - Toán, Hóa, Tiếng Anh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build(),
            XtTohopMonthi.builder()
                .matohop("D09").mon1("TO").mon2("SU").mon3("AN")
                .tentohop("Khối D09 - Toán, Lịch sử, Tiếng Anh")
                .createdAt(LocalDate.now()).updatedAt(LocalDate.now()).build()
        );

        subjectGroupRepository.saveAll(groups);
        log.info("Seeded {} subject groups successfully.", groups.size());
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

        // VẬT LÍ - dùng LI để match entity
        records.add(createRecord("VSAT", "LI", 123.0, 147.0, 9.5, 10.0, now));
        records.add(createRecord("VSAT", "LI", 118.5, 123.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "LI", 112.5, 118.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "LI", 105.0, 112.5, 8.5, 9.0, now));
        records.add(createRecord("VSAT", "LI", 99.5, 105.0, 8.0, 8.5, now));
        records.add(createRecord("VSAT", "LI", 94.5, 99.5, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "LI", 90.0, 94.5, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "LI", 85.0, 90.0, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "LI", 80.0, 85.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "LI", 74.0, 80.0, 6.35, 6.75, now));
        records.add(createRecord("VSAT", "LI", 66.5, 74.0, 5.75, 6.35, now));
        records.add(createRecord("VSAT", "LI", 17.0, 66.5, 3.05, 5.75, now));

        // HÓA HỌC - dùng HO để match entity
        records.add(createRecord("VSAT", "HO", 129.0, 150.0, 9.5, 10.0, now));
        records.add(createRecord("VSAT", "HO", 124.5, 129.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "HO", 117.0, 124.5, 8.75, 9.25, now));
        records.add(createRecord("VSAT", "HO", 107.5, 117.0, 8.25, 8.75, now));
        records.add(createRecord("VSAT", "HO", 100.5, 107.5, 7.75, 8.25, now));
        records.add(createRecord("VSAT", "HO", 94.0, 100.5, 7.25, 7.75, now));
        records.add(createRecord("VSAT", "HO", 88.0, 94.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "HO", 81.5, 88.0, 6.25, 6.75, now));
        records.add(createRecord("VSAT", "HO", 75.5, 81.5, 5.75, 6.25, now));
        records.add(createRecord("VSAT", "HO", 68.5, 75.5, 5.25, 5.75, now));
        records.add(createRecord("VSAT", "HO", 59.5, 68.5, 4.6, 5.25, now));
        records.add(createRecord("VSAT", "HO", 20.0, 59.5, 1.35, 4.6, now));

        // SINH HỌC
        records.add(createRecord("VSAT", "SI", 130.5, 150.0, 9.0, 9.75, now));
        records.add(createRecord("VSAT", "SI", 126.5, 130.5, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "SI", 120.5, 126.5, 8.34, 8.75, now));
        records.add(createRecord("VSAT", "SI", 112.5, 120.5, 7.85, 8.34, now));
        records.add(createRecord("VSAT", "SI", 105.5, 112.5, 7.5, 7.85, now));
        records.add(createRecord("VSAT", "SI", 100.0, 105.5, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "SI", 94.5, 100.0, 6.85, 7.25, now));
        records.add(createRecord("VSAT", "SI", 88.5, 94.5, 6.5, 6.85, now));
        records.add(createRecord("VSAT", "SI", 82.5, 88.5, 6.25, 6.5, now));
        records.add(createRecord("VSAT", "SI", 76.0, 82.5, 5.85, 6.25, now));
        records.add(createRecord("VSAT", "SI", 66.5, 76.0, 5.25, 5.85, now));
        records.add(createRecord("VSAT", "SI", 26.5, 66.5, 2.8, 5.25, now));

        // LỊCH SỬ
        records.add(createRecord("VSAT", "SU", 133.5, 150.0, 9.75, 10.0, now));
        records.add(createRecord("VSAT", "SU", 131.0, 133.5, 9.5, 9.75, now));
        records.add(createRecord("VSAT", "SU", 126.5, 131.0, 9.25, 9.5, now));
        records.add(createRecord("VSAT", "SU", 120.5, 126.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "SU", 115.0, 120.5, 8.5, 9.0, now));
        records.add(createRecord("VSAT", "SU", 110.0, 115.0, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "SU", 105.5, 110.0, 8.0, 8.25, now));
        records.add(createRecord("VSAT", "SU", 101.0, 105.5, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "SU", 95.5, 101.0, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "SU", 88.5, 95.5, 7.0, 7.5, now));
        records.add(createRecord("VSAT", "SU", 79.5, 88.5, 6.35, 7.0, now));
        records.add(createRecord("VSAT", "SU", 36.5, 79.5, 2.95, 6.35, now));

        // ĐỊA LÍ
        records.add(createRecord("VSAT", "DI", 124.0, 141.0, 10.0, 10.0, now));
        records.add(createRecord("VSAT", "DI", 120.5, 124.0, 10.0, 10.0, now));
        records.add(createRecord("VSAT", "DI", 115.5, 120.5, 9.75, 10.0, now));
        records.add(createRecord("VSAT", "DI", 108.5, 115.5, 9.25, 9.75, now));
        records.add(createRecord("VSAT", "DI", 103.0, 108.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "DI", 98.5, 103.0, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "DI", 94.0, 98.5, 8.5, 8.75, now));
        records.add(createRecord("VSAT", "DI", 89.5, 94.0, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "DI", 84.5, 89.5, 7.75, 8.25, now));
        records.add(createRecord("VSAT", "DI", 79.0, 84.5, 7.25, 7.75, now));
        records.add(createRecord("VSAT", "DI", 71.0, 79.0, 6.5, 7.25, now));
        records.add(createRecord("VSAT", "DI", 31.0, 71.0, 3.0, 6.5, now));

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
        records.add(createRecord("VSAT", "VA", 129.5, 146.0, 9.25, 9.75, now));
        records.add(createRecord("VSAT", "VA", 127.5, 129.5, 9.0, 9.25, now));
        records.add(createRecord("VSAT", "VA", 124.0, 127.5, 9.0, 9.0, now));
        records.add(createRecord("VSAT", "VA", 119.5, 124.0, 8.75, 9.0, now));
        records.add(createRecord("VSAT", "VA", 115.5, 119.5, 8.5, 8.75, now));
        records.add(createRecord("VSAT", "VA", 112.5, 115.5, 8.25, 8.5, now));
        records.add(createRecord("VSAT", "VA", 109.0, 112.5, 8.0, 8.25, now));
        records.add(createRecord("VSAT", "VA", 106.0, 109.0, 7.75, 8.0, now));
        records.add(createRecord("VSAT", "VA", 102.0, 106.0, 7.5, 7.75, now));
        records.add(createRecord("VSAT", "VA", 97.0, 102.0, 7.25, 7.5, now));
        records.add(createRecord("VSAT", "VA", 90.0, 97.0, 6.75, 7.25, now));
        records.add(createRecord("VSAT", "VA", 5.0, 90.0, 3.5, 6.75, now));

        return records;
    }

    private List<XtBangquydoi> seedThptData() {
        List<XtBangquydoi> records = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // THPT có cùng cấu trúc với VSAT, chỉ khác phương thức - dùng LI, HO để match entity
        String[] subjects = { "TO", "LI", "HO", "SI", "SU", "DI", "AN", "VA" };
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

        // ĐGNL: Quy đổi từ thang 1200 (NL1) về thang 30
        // Bảng quy đổi: [diemA, diemB] = khoảng điểm NL1, [diemC, diemD] = điểm quy đổi thang 30
        double[][] data = {
                // NL1: 1080-1200 → 27.0-30.0 (Xuất sắc)
                { 1080.0, 1200.0, 27.0, 30.0 },
                // NL1: 990-1080 → 24.75-27.0 (Giỏi)
                { 990.0, 1080.0, 24.75, 27.0 },
                // NL1: 900-990 → 22.5-24.75 (Khá)
                { 900.0, 990.0, 22.5, 24.75 },
                // NL1: 810-900 → 20.25-22.5 (Trung bình khá)
                { 810.0, 900.0, 20.25, 22.5 },
                // NL1: 720-810 → 18.0-20.25 (Trung bình)
                { 720.0, 810.0, 18.0, 20.25 },
                // NL1: 630-720 → 15.75-18.0 (Yếu)
                { 630.0, 720.0, 15.75, 18.0 },
                // NL1: 540-630 → 13.5-15.75 (Yếu)
                { 540.0, 630.0, 13.5, 15.75 },
                // NL1: 450-540 → 11.25-13.5 (Kém)
                { 450.0, 540.0, 11.25, 13.5 },
                // NL1: 360-450 → 9.0-11.25 (Kém)
                { 360.0, 450.0, 9.0, 11.25 },
                // NL1: 270-360 → 6.75-9.0 (Yếu)
                { 270.0, 360.0, 6.75, 9.0 },
                // NL1: 180-270 → 4.5-6.75 (Kém)
                { 180.0, 270.0, 4.5, 6.75 },
                // NL1: 0-180 → 0.0-4.5 (Yếu)
                { 0.0, 180.0, 0.0, 4.5 }
        };

        for (double[] row : data) {
            records.add(createRecordNoToHop("DGNL", "NL1", row[0], row[1], row[2], row[3], now));
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