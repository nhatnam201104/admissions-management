package com.example.managementadmissionwf.ui.panel.admission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AdmissionResultController {

    private AdmissionPanel admissionPanel;
    private ResultTable resultTable;

    @Autowired
    private AdmissionResultService service;
    
    public void setAdmissionPanel(AdmissionPanel admissionPanel, ResultTable resultTable) {
        this.admissionPanel = admissionPanel;
        this.resultTable = resultTable;
    }
    
    public void loadResults() {
        List<AdmissionResultDTO> list = service.getAllResults();
        updateTable(list);
    }

    // 2. Tìm kiếm
    public void search(String keyword, String ketQua, String nganh) {
        List<AdmissionResultDTO> list = service.search(keyword, ketQua, nganh);
        updateTable(list);
    }

    // 3. Cập nhật kết quả (Admin)
    public void updateResult() {
        int row = resultTable.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(admissionPanel, "Vui lòng chọn 1 thí sinh trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy ID từ cột STT (Cột 0)
        Integer id = (Integer) resultTable.getTable().getValueAt(row, 0);

        String[] options = {"TRUNG_TUYEN", "TRUOT", "CHO_XET"};
        int choice = JOptionPane.showOptionDialog(admissionPanel, "Chọn trạng thái mới:", "Cập nhật Kết Quả", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice >= 0) {
            service.updateResult(id, options[choice]);
            loadResults(); // Tải lại bảng
            JOptionPane.showMessageDialog(admissionPanel, "Cập nhật thành công!");
        }
    }

    // 4. Xuất Excel
    public void handleExportExcel() {
        // 1. Khởi tạo và hiển thị Dialog
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        ResultExportDialog dialog = new ResultExportDialog(parentFrame, "Excel (.xlsx)");
        dialog.setVisible(true); 

        // 2. Nếu người dùng bấm "Export" (confirmed = true)
        if (dialog.isConfirmed()) {
            // Lấy danh sách cần xuất
            List<AdmissionResultDTO> listToExport = service.getAllResults();
            
            System.out.println("=== ĐANG XUẤT EXCEL ===");
            System.out.println("Phạm vi: " + dialog.getSelectedScope());
            System.out.println("Số cột được chọn: " + dialog.getSelectedColumns().size());
            
            // Gọi Service để xử lý xuất file
            service.exportToExcel(listToExport); 
            
            JOptionPane.showMessageDialog(admissionPanel, 
                "Đã xuất file Excel thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 5. Xuất PDF
    public void handleExportPDF() {
        // 1. Khởi tạo và hiển thị Dialog
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(admissionPanel);
        ResultExportDialog dialog = new ResultExportDialog(parentFrame, "PDF (.pdf)");
        dialog.setVisible(true); // Mở Dialog

        // 2. Nếu người dùng bấm "Export"
        if (dialog.isConfirmed()) {
            List<AdmissionResultDTO> listToExport = service.getAllResults();
            
            System.out.println("=== ĐANG XUẤT PDF ===");
            System.out.println("Phạm vi: " + dialog.getSelectedScope());
            
            service.exportToPDF(listToExport); 
            
            JOptionPane.showMessageDialog(admissionPanel, 
                "Đã xuất file PDF thành công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Đổ dữ liệu từ List DTO vào Bảng
    private void updateTable(List<AdmissionResultDTO> list) {
        DefaultTableModel model = resultTable.getTableModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (AdmissionResultDTO dto : list) {
            String ngayXetStr = (dto.getNgayXet() != null) ? dto.getNgayXet().format(dtf) : "";
            model.addRow(new Object[]{
                dto.getId(), dto.getCccd(), dto.getHoTen(), dto.getSobaodanh(),
                dto.getNvTt(), dto.getTennganh(), dto.getPhuongThuc(),
                dto.getDiemXettuyen(), dto.getKetQua(), ngayXetStr
            });
        }
    }
}