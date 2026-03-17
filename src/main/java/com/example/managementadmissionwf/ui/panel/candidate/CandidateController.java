package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for Candidate Management
 * Handles business logic for CandidatePanel
 */
@Component
public class CandidateController {
    
    @Autowired
    private CandidateService candidateService;
    
    private CandidatePanel candidatePanel;
    private CandidateListPanel listPanel;
    
    public void setCandidatePanel(CandidatePanel candidatePanel) {
        this.candidatePanel = candidatePanel;
        this.listPanel = candidatePanel.getListPanel();
    }
    
    /**
     * Load all candidates to table
     */
    public void loadAllCandidates() {
        List<CandidateDTO> candidates = candidateService.getAllCandidates();
        listPanel.loadData(candidates);
    }
    
    /**
     * Search candidates
     */
    public void searchCandidates(String keyword, String khuVuc, String doiTuong) {
        List<CandidateDTO> candidates = candidateService.searchCandidates(keyword, khuVuc, doiTuong);
        listPanel.loadData(candidates);
    }
    
    /**
     * Add new candidate
     */
    public void addCandidate() {
        CandidateFormDialog dialog = new CandidateFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(candidatePanel),
            "Thêm Thí Sinh Mới",
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                CandidateDTO newCandidate = dialog.getCandidate();
                candidateService.createCandidate(newCandidate);
                loadAllCandidates();
                JOptionPane.showMessageDialog(candidatePanel, "Thêm thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Edit selected candidate
     */
    public void editCandidate() {
        CandidateDTO selected = listPanel.getSelectedCandidate();
        if (selected == null) {
            JOptionPane.showMessageDialog(candidatePanel, "Vui lòng chọn thí sinh cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CandidateFormDialog dialog = new CandidateFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(candidatePanel),
            "Sửa Thông Tin Thí Sinh",
            selected
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                CandidateDTO updatedCandidate = dialog.getCandidate();
                candidateService.updateCandidate(updatedCandidate);
                loadAllCandidates();
                JOptionPane.showMessageDialog(candidatePanel, "Cập nhật thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Delete selected candidate
     */
    public void deleteCandidate() {
        CandidateDTO selected = listPanel.getSelectedCandidate();
        if (selected == null) {
            JOptionPane.showMessageDialog(candidatePanel, "Vui lòng chọn thí sinh cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            candidatePanel,
            "Bạn có chắc chắn muốn xóa thí sinh:\n" + selected.getHoTen() + "\nCCCD: " + selected.getCccd() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                candidateService.deleteCandidate(selected.getCccd());
                loadAllCandidates();
                JOptionPane.showMessageDialog(candidatePanel, "Xóa thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh data
     */
    public void refreshData() {
        loadAllCandidates();
        JOptionPane.showMessageDialog(candidatePanel, "Dữ liệu đã được làm mới!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}