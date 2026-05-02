package com.example.managementadmissionwf.ui.panel.conversion;

import com.example.managementadmissionwf.bus.interfaces.ConversionTableService;
import com.example.managementadmissionwf.dto.ConversionTableDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ConversionTableController {

    private final ConversionTableService conversionTableService;
    private ConversionTableListPanel view;

    public ConversionTableController(ConversionTableService conversionTableService) {
        this.conversionTableService = conversionTableService;
    }

    public void setView(ConversionTableListPanel view) {
        this.view = view;
    }

    public void loadConversionTables(String phuongThuc, String toHop, String mon,
            String keyword, DefaultTableModel model, int page, int size) {
        try {
            Paging<ConversionTableDTO> paging = conversionTableService.search(
                    phuongThuc, toHop, mon, keyword, page, size);
            populateTable(model, paging.getData());
            if (view != null)
                view.updatePagination(paging);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public void addConversionTable(ConversionTableDTO dto) {
        conversionTableService.create(dto);
        if (view != null)
            view.refreshData();
    }

    public void updateConversionTable(Integer id, ConversionTableDTO dto) {
        conversionTableService.update(id, dto);
        if (view != null)
            view.refreshData();
    }

    public void deleteConversionTable(Integer id) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn xóa bản ghi này không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conversionTableService.delete(id);
                JOptionPane.showMessageDialog(view, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                if (view != null)
                    view.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Không thể xóa: " + e.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportToExcel(OutputStream outputStream, String phuongThuc, String toHop, String mon, String keyword) {
        try {
            conversionTableService.exportExcel(outputStream, phuongThuc, toHop, mon, keyword);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất file Excel: " + e.getMessage(), e);
        }
    }

    public ImportResult<ConversionTableDTO> importFromExcel(InputStream inputStream) {
        return conversionTableService.importExcel(inputStream);
    }

    public List<String> getAllPhuongThuc() {
        return conversionTableService.getAllPhuongThuc();
    }

    public List<String> getAllToHop() {
        return conversionTableService.getAllToHop();
    }

    public List<String> getAllMon() {
        return conversionTableService.getAllMon();
    }

    private void populateTable(DefaultTableModel model, List<ConversionTableDTO> list) {
        model.setRowCount(0);
        int stt = 1;
        for (ConversionTableDTO dto : list) {
            model.addRow(new Object[] {
                    dto.getId(), // Column 0 - ID (hidden)
                    stt++, // Column 1 - STT
                    dto.getPhuongThuc(), // Column 2 - Phương thức
                    dto.getToHop() != null ? dto.getToHop() : "-", // Column 3 - Tổ hợp
                    dto.getMon(), // Column 4 - Môn
                    dto.getDiemA(), // Column 5 - Điểm A
                    dto.getDiemB(), // Column 6 - Điểm B
                    dto.getDiemC(), // Column 7 - Điểm C
                    dto.getDiemD() // Column 8 - Điểm D
            });
        }
    }
}