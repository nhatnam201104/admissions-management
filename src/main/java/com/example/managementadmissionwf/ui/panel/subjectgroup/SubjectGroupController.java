package com.example.managementadmissionwf.ui.panel.subjectgroup;

import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Controller for Subject Group Management
 * Handles business logic and UI interactions
 */
@Component
public class SubjectGroupController {
    
    private SubjectGroupPanel subjectGroupPanel;
    private SubjectGroupListPanel listPanel;
    
    public void setSubjectGroupPanel(SubjectGroupPanel panel) {
        this.subjectGroupPanel = panel;
        this.listPanel = panel.getListPanel();
    }
    
    /**
     * Load all subject groups
     */
    public void loadAllSubjectGroups() {
        // In real implementation, this would load from database
        // For now, data is already loaded in ListPanel constructor
        System.out.println("Loading all subject groups...");
    }
    
    /**
     * Search subject groups by keyword
     */
    public void searchSubjectGroups(String keyword) {
        System.out.println("Searching subject groups with keyword: " + keyword);
        
        // TODO: Implement actual search logic
        // For now, just show message
        JOptionPane.showMessageDialog(
            subjectGroupPanel,
            "Tìm kiếm: " + keyword + "\n(Tính năng đang phát triển)",
            "Tìm kiếm",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Add new subject group
     */
    public void addSubjectGroup() {
        String[] result = SubjectGroupFormDialog.showAddDialog(
            (JFrame) SwingUtilities.getWindowAncestor(subjectGroupPanel)
        );
        
        if (result != null) {
            // Add new row to table
            listPanel.addRow(result);
            
            JOptionPane.showMessageDialog(
                subjectGroupPanel,
                "Đã thêm tổ hợp môn thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Edit selected subject group
     */
    public void editSubjectGroup() {
        int selectedRow = listPanel.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                subjectGroupPanel,
                "Vui lòng chọn một tổ hợp môn để sửa!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Get current data
        JTable table = listPanel.getTable();
        String[] currentData = new String[4];
        for (int i = 0; i < 4; i++) {
            currentData[i] = (String) table.getValueAt(selectedRow, i);
        }
        
        // Show edit dialog
        String[] result = SubjectGroupFormDialog.showEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(subjectGroupPanel),
            currentData
        );
        
        if (result != null) {
            // Update row
            for (int i = 0; i < 4; i++) {
                table.setValueAt(result[i], selectedRow, i);
            }
            
            JOptionPane.showMessageDialog(
                subjectGroupPanel,
                "Đã cập nhật tổ hợp môn thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Delete selected subject group
     */
    public void deleteSubjectGroup() {
        int selectedRow = listPanel.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                subjectGroupPanel,
                "Vui lòng chọn một tổ hợp môn để xóa!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            subjectGroupPanel,
            "Bạn có chắc chắn muốn xóa tổ hợp môn này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            listPanel.getTableModel().removeRow(selectedRow);
            
            JOptionPane.showMessageDialog(
                subjectGroupPanel,
                "Đã xóa tổ hợp môn thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Refresh data
     */
    public void refreshData() {
        listPanel.clearData();
        listPanel = new SubjectGroupListPanel();
        
        // TODO: Reload from database
        JOptionPane.showMessageDialog(
            subjectGroupPanel,
            "Đã làm mới dữ liệu!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}