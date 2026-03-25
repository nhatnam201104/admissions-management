package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dto.MajorDTO;
import com.example.managementadmissionwf.dto.MajorTohopDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MajorServiceImpl implements MajorService {

    // 1. MOCK DATABASE
    private final Map<Integer, MajorDTO> majorDatabase = new ConcurrentHashMap<>();
    
    // Bộ đếm giả lập Auto Increment ID của Database
    private final AtomicInteger majorIdCounter = new AtomicInteger(1);
    private final AtomicInteger tohopIdCounter = new AtomicInteger(1);

    // 2. KHỞI TẠO DỮ LIỆU MẪU (Initialization)
    @PostConstruct
    public void init() {
        log.info("Khởi tạo Mock Data: Bắt đầu nạp dữ liệu Ngành và Tổ hợp...");

        MajorDTO cntt = new MajorDTO();
        cntt.setIdNganh(majorIdCounter.getAndIncrement());
        cntt.setMaNganh("7480201");
        cntt.setTenNganh("Công nghệ thông tin");
        cntt.setNChiTieu(500);
        cntt.setNDiemSan(18.0);
        cntt.setTohopList(new ArrayList<>());

        MajorDTO kdqt = new MajorDTO();
        kdqt.setIdNganh(majorIdCounter.getAndIncrement());
        kdqt.setMaNganh("7340120");
        kdqt.setTenNganh("Kinh doanh quốc tế");
        kdqt.setNChiTieu(200);
        kdqt.setNDiemSan(20.0);
        kdqt.setTohopList(new ArrayList<>());

        // Map Tổ hợp vào Ngành CNTT
        MajorTohopDTO tohopA00 = new MajorTohopDTO(
                tohopIdCounter.getAndIncrement(), "7480201", "A00",
                "Toán", 2.0, "Vật lý", 1.0, "Hóa học", 1.0);
        MajorTohopDTO tohopA01 = new MajorTohopDTO(
                tohopIdCounter.getAndIncrement(), "7480201", "A01",
                "Toán", 2.0, "Vật lý", 1.0, "Tiếng Anh", 1.0);
        
        cntt.getTohopList().add(tohopA00);
        cntt.getTohopList().add(tohopA01);

        // Lưu vào Mock Database
        majorDatabase.put(cntt.getIdNganh(), cntt);
        majorDatabase.put(kdqt.getIdNganh(), kdqt);

        log.info("Khởi tạo Mock Data thành công. Đang có {} ngành trong hệ thống.", majorDatabase.size());
    }

    // 3. CRUD NGÀNH (MAJOR MANAGEMENT)

    @Override
    public List<MajorDTO> getAllMajors() {
        log.debug("Lấy danh sách toàn bộ ngành học.");
        return new ArrayList<>(majorDatabase.values());
    }

    @Override
    public MajorDTO getMajorByCode(String maNganh) {
        log.debug("Tìm kiếm ngành bằng mã: {}", maNganh);
        return majorDatabase.values().stream()
                .filter(m -> m.getMaNganh().equalsIgnoreCase(maNganh))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ngành có mã " + maNganh));
    }

    @Override
    public List<MajorDTO> searchMajors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMajors();
        }
        String kw = keyword.toLowerCase();
        return majorDatabase.values().stream()
                .filter(m -> m.getTenNganh().toLowerCase().contains(kw) || 
                             m.getMaNganh().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    @Override
    public MajorDTO createMajor(MajorDTO dto) {
        log.info("Yêu cầu thêm mới ngành: {}", dto.getTenNganh());
        
        // Kiểm tra trùng mã ngành
        boolean isDuplicate = majorDatabase.values().stream()
                .anyMatch(m -> m.getMaNganh().equalsIgnoreCase(dto.getMaNganh()));
        if (isDuplicate) {
            throw new RuntimeException("Lỗi: Mã ngành " + dto.getMaNganh() + " đã tồn tại trong hệ thống.");
        }

        // Cấp phát ID và đảm bảo list tổ hợp không bị null
        dto.setIdNganh(majorIdCounter.getAndIncrement());
        if (dto.getTohopList() == null) {
            dto.setTohopList(new ArrayList<>());
        }

        majorDatabase.put(dto.getIdNganh(), dto);
        log.info("Thêm mới thành công. ID cấp phát: {}", dto.getIdNganh());
        return dto;
    }

    @Override
    public MajorDTO updateMajor(MajorDTO dto) {
        Integer idNganh = dto.getIdNganh(); 
        
        log.info("Yêu cầu cập nhật thông tin ngành ID: {}", idNganh);

        // 1. Kiểm tra an toàn: Đảm bảo DTO gửi lên có chứa ID
        if (idNganh == null) {
            throw new RuntimeException("Lỗi: Không thể cập nhật vì thiếu ID ngành (idNganh is null).");
        }
        
        // 2. Kiểm tra xem ID này có tồn tại trong Database không
        if (!majorDatabase.containsKey(idNganh)) {
            throw new RuntimeException("Lỗi: Không tìm thấy ngành với ID " + idNganh + " để cập nhật.");
        }

        // 3. Nếu client không gửi lên tohopList, giữ nguyên tohopList cũ để không bị ghi đè mất data
        if (dto.getTohopList() == null) {
            dto.setTohopList(majorDatabase.get(idNganh).getTohopList());
        }

        // 4. Cập nhật vào Mock Database
        majorDatabase.put(idNganh, dto);
        log.info("Cập nhật thành công ngành ID: {}", idNganh);
        
        return dto;
    }

    @Override
    public void deleteMajor(Integer id) {
        log.warn("Yêu cầu xóa ngành ID: {}", id);
        if (majorDatabase.remove(id) == null) {
            throw new RuntimeException("Lỗi: Không thể xóa vì ID " + id + " không tồn tại.");
        }
        log.info("Xóa thành công ngành ID: {}", id);
    }

    // 4. QUẢN LÝ MAPPING (TỔ HỢP - NGÀNH)

    @Override
    public void addSubjectGroup(String maNganh, String maTohop) {
        log.info("Yêu cầu thêm tổ hợp {} vào ngành {}", maTohop, maNganh);
        MajorDTO major = getMajorByCode(maNganh);

        // Chặn thêm trùng tổ hợp vào cùng 1 ngành
        boolean isExist = major.getTohopList().stream()
                .anyMatch(th -> th.getMaToHop().equalsIgnoreCase(maTohop));
        if (isExist) {
            throw new RuntimeException("Lỗi: Tổ hợp " + maTohop + " đã có sẵn trong ngành " + maNganh);
        }

        // Mock dữ liệu cho tổ hợp mới
        MajorTohopDTO newTohop = new MajorTohopDTO();
        newTohop.setId(tohopIdCounter.getAndIncrement());
        newTohop.setMaNganh(maNganh);
        newTohop.setMaToHop(maTohop);
        newTohop.setThMon1("Môn 1"); newTohop.setHsMon1(1.0);
        newTohop.setThMon2("Môn 2"); newTohop.setHsMon2(1.0);
        newTohop.setThMon3("Môn 3"); newTohop.setHsMon3(1.0);

        major.getTohopList().add(newTohop);
        log.info("Thêm tổ hợp thành công.");
    }

    @Override
    public void removeSubjectGroup(Integer tohopId) {
        log.warn("Yêu cầu gỡ bỏ tổ hợp có ID: {}", tohopId);
        boolean isRemoved = false;

        // Quét toàn bộ database các ngành để tìm và xóa tổ hợp có ID trùng khớp
        for (MajorDTO major : majorDatabase.values()) {
            isRemoved = major.getTohopList().removeIf(th -> th.getId().equals(tohopId));
            if (isRemoved) {
                log.info("Đã gỡ bỏ tổ hợp ID {} khỏi ngành {}", tohopId, major.getMaNganh());
                break;
            }
        }

        if (!isRemoved) {
            throw new RuntimeException("Lỗi: Không tìm thấy tổ hợp với ID " + tohopId + " để xóa.");
        }
    }
}