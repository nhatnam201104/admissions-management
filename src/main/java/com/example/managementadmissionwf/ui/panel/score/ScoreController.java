package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller for Score Management
 * Handles business logic for ScorePanel
 */
@Component
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    private ScorePanel scorePanel;
    private ScoreListPanel listPanel;
    
    public void setScorePanel(ScorePanel scorePanel) {
        this.scorePanel = scorePanel;
        this.listPanel = scorePanel.getListPanel();
    }
    
    /**
     * Load all scores to table
     */
    public void loadAllScores() {
        List<ScoreDTO> scores = scoreService.getAllScores();
        listPanel.loadData(scores);
    }
    
    /**
     * Search scores
     */
    public void searchScores(String keyword, String phuongThuc) {
//        List<ScoreDTO> scores = scoreService.(keyword, phuongThuc);
//        listPanel.loadData(scores);
    }
    
    /**
     * Add new score
     */
    public void addScore() {
        // Show dialog to select candidate first
        String cccd = JOptionPane.showInputDialog(
            scorePanel,
            "Nhập CCCD của thí sinh:",
            "Chọn Thí Sinh",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (cccd == null || cccd.trim().isEmpty()) {
            return;
        }
        
        ScoreDTO score = new ScoreDTO();
        score.setCccd(cccd.trim());
        score.setSobaodanh(""); // Will be filled by service
        
        ScoreFormDialog dialog = new ScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel),
            "Thêm Điểm Thí Sinh",
            score
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                ScoreDTO newScore = dialog.getScore();
                scoreService.createScore(newScore);
                loadAllScores();
                JOptionPane.showMessageDialog(scorePanel, "Thêm điểm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(scorePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Edit selected score
     */
    public void editScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            JOptionPane.showMessageDialog(scorePanel, "Vui lòng chọn điểm cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ScoreFormDialog dialog = new ScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel),
            "Sửa Điểm Thí Sinh",
            selected
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            try {
                ScoreDTO updatedScore = dialog.getScore();
                scoreService.updateScore(updatedScore);
                loadAllScores();
                JOptionPane.showMessageDialog(scorePanel, "Cập nhật điểm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(scorePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Delete selected score
     */
    public void deleteScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            JOptionPane.showMessageDialog(scorePanel, "Vui lòng chọn điểm cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            scorePanel,
            "Bạn có chắc chắn muốn xóa điểm của thí sinh:\nCCCD: " + selected.getCccd() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                scoreService.deleteScore(selected.getCccd());
                loadAllScores();
                JOptionPane.showMessageDialog(scorePanel, "Xóa điểm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(scorePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh data
     */
    public void refreshData() {
        loadAllScores();
        JOptionPane.showMessageDialog(scorePanel, "Dữ liệu đã được làm mới!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}