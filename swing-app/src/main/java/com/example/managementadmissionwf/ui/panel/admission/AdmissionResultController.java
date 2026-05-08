package com.example.managementadmissionwf.ui.panel.admission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.bus.interfaces.StatisticService;
import com.example.managementadmissionwf.config.ApplicationContextHolder;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class AdmissionResultController {

    private AdmissionPanel admissionPanel;
    private ResultTable resultTable;

    @Autowired
    private AdmissionResultService service;
    
    @Autowired
    private StatisticService statisticService;
    
    public void setAdmissionPanel(AdmissionPanel admissionPanel, ResultTable resultTable) {
        this.admissionPanel = admissionPanel;
        this.resultTable = resultTable;
    }
    
    public void loadResults() {
        List<AdmissionResultDTO> list = service.getAllResults();
        updateTable(list);
        updateStatistics();
    }

    public void search(String keyword, String ketQua, String nganh, String phuongThuc) {
        List<AdmissionResultDTO> list = service.search(keyword, ketQua, nganh, phuongThuc);
        updateTable(list);
        updateStatistics();
    }
    
    private void updateStatistics() {
        try {
            StatisticSummary summary = statisticService.getSummary();
            long totalAdmitted = summary.admitted();
            double admissionRate = summary.admissionRate();
            admissionPanel.updateStatistics(totalAdmitted, admissionRate);
        } catch (Exception e) {
            // Ignore statistics errors, table is more important
        }
    }

    public void updateResult() {
        int row = resultTable.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(admissionPanel, "Vui lòng chọn 1 thí sinh trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) resultTable.getTable().getValueAt(row, 0);

        String[] options = {"TRUNG_TUYEN", "TRUOT", "CHO_XET"};
        int choice = JOptionPane.showOptionDialog(admissionPanel, "Chọn trạng thái mới:", "Cập nhật Kết Quả", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice >= 0) {
            service.updateResult(id, options[choice]);
            loadResults();
            JOptionPane.showMessageDialog(admissionPanel, "Cập nhật thành công!");
        }
    }

    public void handleExportExcel() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        ResultExportDialog dialog = new ResultExportDialog(parentFrame, "Excel (.xlsx)");
        dialog.setVisible(true); 

        if (dialog.isConfirmed()) {
            List<AdmissionResultDTO> listToExport = service.getAllResults();
            service.exportToExcel(listToExport); 
            JOptionPane.showMessageDialog(admissionPanel, 
                "Đã xuất file Excel thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void handleExportPDF() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        ResultExportDialog dialog = new ResultExportDialog(parentFrame, "PDF (.pdf)");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            List<AdmissionResultDTO> listToExport = service.getAllResults();
            service.exportToPDF(listToExport); 
            JOptionPane.showMessageDialog(admissionPanel, 
                "Đã xuất file PDF thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() {
        loadResults();
    }

    public void handleAutomaticAdmission() {
        int confirm = JOptionPane.showConfirmDialog(admissionPanel, 
            "Bạn có chắc muốn xét tuyển tự động cho tất cả nguyện vọng?\n" +
            "Hệ thống sẽ tính điểm chuẩn dựa trên chỉ tiêu và cập nhật kết quả.",
            "Xác nhận xét tuyển", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int count = service.handleAutomaticAdmission();
                loadResults();
                JOptionPane.showMessageDialog(admissionPanel, 
                    "Xét tuyển hoàn thành!\n" + count + " thí sinh trúng tuyển.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(admissionPanel, 
                    "Lỗi khi xét tuyển: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addAspiration() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        AspirationFormDialog dialog = new AspirationFormDialog(parentFrame, this);
        dialog.setVisible(true);
    }

    public void showScoreDetail() {
        int row = resultTable.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(admissionPanel, "Vui lòng chọn 1 thí sinh trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) resultTable.getTable().getValueAt(row, 0);

        // Chỉ cần ID - service sẽ lấy tất cả thông tin từ nguyện vọng
        Map<String, Object> details = service.getScoreDetails(id);
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        ScoreDetailDialog dialog = new ScoreDetailDialog(parentFrame, details);
        dialog.setVisible(true);
    }

    public void editAspiration() {
        int row = resultTable.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(admissionPanel, "Vui lòng chọn 1 nguyện vọng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) resultTable.getTable().getValueAt(row, 0);
        NguyenVongRepository repo = ApplicationContextHolder.getBean(NguyenVongRepository.class);
        
        repo.findById(id).ifPresentOrElse(nv -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
            AspirationFormDialog dialog = new AspirationFormDialog(parentFrame, this, nv);
            dialog.setVisible(true);
        }, () -> {
            JOptionPane.showMessageDialog(admissionPanel, "Không tìm thấy nguyện vọng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void deleteAspiration() {
        int row = resultTable.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(admissionPanel, "Vui lòng chọn 1 nguyện vọng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) resultTable.getTable().getValueAt(row, 0);
        NguyenVongRepository repo = ApplicationContextHolder.getBean(NguyenVongRepository.class);
        
        repo.findById(id).ifPresentOrElse(nv -> {
            // Kiểm tra trạng thái - không cho xóa NV đã xét (TRUNG_TUYEN hoặc TRUOT)
            String ketQua = nv.getNvKetqua();
            if (ketQua != null && !("CHO_XET".equals(ketQua) || "CHO_XET" == ketQua)) {
                JOptionPane.showMessageDialog(admissionPanel, 
                    "Không thể xóa nguyện vọng đã được xét tuyển!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(admissionPanel, 
                "Bạn có chắc muốn xóa nguyện vọng này?", "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                nv.setIsDeleted(true);
                repo.save(nv);
                loadResults();
                JOptionPane.showMessageDialog(admissionPanel, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }, () -> {
            JOptionPane.showMessageDialog(admissionPanel, "Không tìm thấy nguyện vọng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void updateTable(List<AdmissionResultDTO> list) {
        DefaultTableModel model = resultTable.getTableModel();
        model.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (AdmissionResultDTO dto : list) {
            // Format điểm XT với 2 số thập phân
            String diemXettuyenStr = dto.getDiemXettuyen() != null 
                ? String.format("%.2f", dto.getDiemXettuyen()) 
                : "0.00";
            // Format điểm chuẩn
            String diemChuanStr = dto.getDiemChuan() != null 
                ? String.format("%.2f", dto.getDiemChuan()) 
                : "-";
            
            model.addRow(new Object[]{
                dto.getId(), dto.getCccd(), dto.getSobaodanh(),
                dto.getNvTt(), dto.getTennganh(), dto.getPhuongThuc(),
                dto.getTohop(), diemXettuyenStr, diemChuanStr,
                dto.getKetQua()
            });
        }
    }
}