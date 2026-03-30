package com.example.managementadmissionwf.ui.panel.subjectgroup;

import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;
import com.example.managementadmissionwf.service.SubjectGroupService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Controller for Subject Group Management
 * Handles business logic and UI interactions
 */
@Component
public class SubjectGroupController {

    private final SubjectGroupService subjectGroupService;

    private SubjectGroupPanel subjectGroupPanel;
    private SubjectGroupListPanel listPanel;

    public SubjectGroupController(SubjectGroupService subjectGroupService) {
        this.subjectGroupService = subjectGroupService;
    }

    public void setSubjectGroupPanel(SubjectGroupPanel panel) {
        this.subjectGroupPanel = panel;
        this.listPanel = panel.getListPanel();
    }

    /**
     * Load subject groups with pagination and keyword
     */
    public void loadSubjectGroups(String keyword, int page, int size) {
        try {
            Paging<SubjectGroupResponse> result = subjectGroupService.search(keyword, page, size);

            // Clear current data
            listPanel.clearData();

            // Update table
            List<SubjectGroupResponse> items = result.getData();
            if (items != null) {
                for (SubjectGroupResponse item : items) {
                    listPanel.addRow(new Object[] {
                            item.getMatohop(),
                            item.getTentohop(),
                            item.getMon1(),
                            item.getMon2(),
                            item.getMon3(),
                            item.getId()
                    });
                }
            }

            // Update pagination UI
            subjectGroupPanel.updatePagination(result);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    subjectGroupPanel,
                    "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Add new subject group
     */
    public void addSubjectGroup() {
        SubjectGroupRequest request = SubjectGroupFormDialog.showAddDialog(
                (JFrame) SwingUtilities.getWindowAncestor(subjectGroupPanel));

        if (request != null) {
            try {
                subjectGroupService.create(request);
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Đã thêm tổ hợp môn thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                subjectGroupPanel.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Lỗi khi thêm: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Edit selected subject group
     */
    public void editSubjectGroup() {
        int selectedRow = listPanel.getSelectedRow();
        Integer selectedId = listPanel.getSelectedId();

        if (selectedRow == -1 || selectedId == null) {
            JOptionPane.showMessageDialog(
                    subjectGroupPanel,
                    "Vui lòng chọn một tổ hợp môn để sửa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Fetch latest data from database
            SubjectGroupResponse currentData = subjectGroupService.findById(selectedId);

            // Show edit dialog
            SubjectGroupRequest request = SubjectGroupFormDialog.showEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(subjectGroupPanel),
                    currentData);

            if (request != null) {
                subjectGroupService.update(selectedId, request);
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Đã cập nhật tổ hợp môn thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                subjectGroupPanel.refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    subjectGroupPanel,
                    "Lỗi khi cập nhật: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delete selected subject group
     */
    public void deleteSubjectGroup() {
        Integer selectedId = listPanel.getSelectedId();

        if (selectedId == null) {
            JOptionPane.showMessageDialog(
                    subjectGroupPanel,
                    "Vui lòng chọn một tổ hợp môn để xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                subjectGroupPanel,
                "Bạn có chắc chắn muốn xóa tổ hợp môn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                subjectGroupService.delete(selectedId);
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Đã xóa tổ hợp môn thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                subjectGroupPanel.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Lỗi khi xóa: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Export to Excel
     */
    public void exportExcel(String keyword) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("Danh_sach_to_hop_mon.xlsx"));

        int userSelection = fileChooser.showSaveDialog(subjectGroupPanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            try (OutputStream os = new FileOutputStream(filePath)) {
                subjectGroupService.exportExcel(os, keyword);
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Đã xuất dữ liệu ra file Excel thành công!\n" + filePath,
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Lỗi khi xuất file Excel: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Import from Excel
     */
    public void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(subjectGroupPanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try (InputStream is = new FileInputStream(fileToOpen)) {
                ImportResult<SubjectGroupResponse> result = subjectGroupService.importExcel(is);

                StringBuilder msg = new StringBuilder();
                msg.append("Kết quả nhập dữ liệu:\n");
                msg.append("- Thành công: ").append(result.getSuccessCount()).append("\n");
                msg.append("- Thất bại: ").append(result.getErrorCount()).append("\n");

                if (result.getErrorCount() > 0 && !result.getErrors().isEmpty()) {
                    msg.append("\nChi tiết lỗi (5 lỗi đầu tiên):\n");
                    int count = 0;
                    for (String err : result.getErrors()) {
                        msg.append("- ").append(err).append("\n");
                        if (++count >= 5)
                            break;
                    }
                }

                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        msg.toString(),
                        "Kết quả nhập Excel",
                        result.getErrorCount() > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

                // Refresh data if there were successful imports
                if (result.getSuccessCount() > 0) {
                    subjectGroupPanel.refreshData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        subjectGroupPanel,
                        "Lỗi khi đọc file Excel: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}