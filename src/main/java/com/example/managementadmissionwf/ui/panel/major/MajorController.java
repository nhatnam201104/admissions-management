package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.common.Paging;
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

    public void loadMajorsWithPaging(String keyword, DefaultTableModel model, int page, int size) {
        try {
            Paging<MajorDTO> paging = majorService.search(keyword, page, size);
            populateMasterTable(model, paging.getData());
            if (view != null) view.updatePagination(paging);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public void addMajor(MajorDTO dto) {
        majorService.create(dto);           // ← để exception lan lên form
        if (view != null) view.refreshData();
    }

    public void updateMajor(String maNganhCu, MajorDTO dto) {
        majorService.update(maNganhCu, dto); // ← để exception lan lên form
        if (view != null) view.refreshData();
    }

    public void deleteMajor(String maNganh) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn xóa ngành học mã [" + maNganh + "] không?\nLưu ý: Thao tác này sẽ đánh dấu xóa ngành trong hệ thống.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                majorService.delete(maNganh);
                JOptionPane.showMessageDialog(view, "Đã xóa ngành [" + maNganh + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                if (view != null) view.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Không thể xóa ngành: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateMasterTable(DefaultTableModel model, List<MajorDTO> list) {
        model.setRowCount(0);
        for (MajorDTO m : list) {
            StringBuilder pt = new StringBuilder();
            if (Boolean.TRUE.equals(m.getThpt())) pt.append("THPT, ");
            if (Boolean.TRUE.equals(m.getDgnl())) pt.append("ĐGNL, ");
            if (Boolean.TRUE.equals(m.getTuyenThang())) pt.append("T.Thẳng, ");
            if (Boolean.TRUE.equals(m.getVsat())) pt.append("VSAT, ");
            String ptStr = pt.length() > 0 ? pt.substring(0, pt.length() - 2) : "Chưa có";
            model.addRow(new Object[]{ m.getMaNganh(), m.getTenNganh(), m.getChiTieu(), m.getDiemSan(), ptStr });
        }
    }
}