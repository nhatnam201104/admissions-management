package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dto.MajorDTO;
import com.example.managementadmissionwf.dto.MajorTohopDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MajorController {

    private final MajorService majorService;
    private MajorListPanel view;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }
    public void setView(MajorListPanel view) {
        this.view = view;
    }
    // 1. HIỂN THỊ DỮ LIỆU (READ)

    public void loadAllMajors(DefaultTableModel masterModel) {
        try {
            List<MajorDTO> majors = majorService.getAllMajors();
            populateMasterTable(masterModel, majors);
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách ngành: " + e.getMessage());
        }
    }

    public void searchMajors(String keyword, DefaultTableModel masterModel) {
        try {
            List<MajorDTO> majors = majorService.searchMajors(keyword);
            populateMasterTable(masterModel, majors);
            if (majors.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào cho: " + keyword, "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    public void refreshDetail(String maNganh, DefaultTableModel detailModel) {
        try {
            detailModel.setRowCount(0); // Xóa dữ liệu cũ ở bảng Detail

            MajorDTO major = majorService.getMajorByCode(maNganh);
            if (major != null && major.getTohopList() != null) {
                for (MajorTohopDTO th : major.getTohopList()) {
                    detailModel.addRow(new Object[]{
                            th.getMaToHop(),
                            th.getThMon1() + " (" + th.getHsMon1() + ")",
                            th.getThMon2() + " (" + th.getHsMon2() + ")",
                            th.getThMon3() + " (" + th.getHsMon3() + ")"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải chi tiết tổ hợp: " + e.getMessage());
        }
    }

    // 2. THAO TÁC VỚI NGÀNH (CRUD MAJOR)

    public void addMajor(MajorDTO dto, DefaultTableModel masterModel) {
        try {
            majorService.createMajor(dto);
            showSuccess("Thêm ngành " + dto.getTenNganh() + " thành công!");
            loadAllMajors(masterModel); 
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void editMajor(MajorDTO dto, DefaultTableModel masterModel) {
        try {
            majorService.updateMajor(dto);
            showSuccess("Cập nhật thông tin ngành thành công!");
            loadAllMajors(masterModel); 
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void deleteMajor(Integer idNganh, DefaultTableModel masterModel, DefaultTableModel detailModel) {
        int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa ngành này? Toàn bộ tổ hợp môn bên trong cũng sẽ bị xóa.", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                majorService.deleteMajor(idNganh);
                showSuccess("Đã xóa ngành thành công!");
                loadAllMajors(masterModel);
                detailModel.setRowCount(0);
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // 3. THAO TÁC VỚI TỔ HỢP MÔN

    public void addSubjectGroup(String maNganh, String maTohop, DefaultTableModel detailModel) {
        try {
            majorService.addSubjectGroup(maNganh, maTohop);
            showSuccess("Thêm tổ hợp " + maTohop + " thành công!");
            refreshDetail(maNganh, detailModel); 
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void removeSubjectGroup(Integer tohopId, String maNganh, DefaultTableModel detailModel) {
        try {
            majorService.removeSubjectGroup(tohopId);
            refreshDetail(maNganh, detailModel);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // 4. HÀM TIỆN ÍCH (UTILITIES)

    private void populateMasterTable(DefaultTableModel masterModel, List<MajorDTO> majors) {
        masterModel.setRowCount(0); // Xóa dữ liệu cũ
        for (MajorDTO m : majors) {
            StringBuilder phuongThuc = new StringBuilder();
            if (Boolean.TRUE.equals(m.getNThpt())) phuongThuc.append("THPT, ");
            if (Boolean.TRUE.equals(m.getNDgnl())) phuongThuc.append("ĐGNL, ");
            if (Boolean.TRUE.equals(m.getNTuyenThang())) phuongThuc.append("Tuyển thẳng, ");
            if (Boolean.TRUE.equals(m.getNVsat())) phuongThuc.append("VSAT, ");
            
            String ptStr = phuongThuc.length() > 0 ? phuongThuc.substring(0, phuongThuc.length() - 2) : "Chưa cập nhật";

            masterModel.addRow(new Object[]{
                    m.getMaNganh(),
                    m.getTenNganh(),
                    m.getNChiTieu(),
                    m.getNDiemSan(),
                    ptStr
            });
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi thực thi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}