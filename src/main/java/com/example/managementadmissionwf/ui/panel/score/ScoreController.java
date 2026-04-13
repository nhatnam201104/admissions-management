package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Controller xử lý logic cho quản lý Điểm thi và Điểm ưu tiên.
 * Tách biệt luồng xử lý giữa Score và BonusScore.
 */
@Component
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private BonusScoreService bonusScoreService;

    private ScorePanel scorePanel;
    private ScoreListPanel listPanel;

    // Trạng thái phân trang
    private int currentPage = 0;
    private int pageSize = 15;

    public void setScorePanel(ScorePanel scorePanel) {
        this.scorePanel = scorePanel;
        this.listPanel = scorePanel.getListPanel();
    }

    // 1. QUẢN LÝ ĐIỂM THI (SCORE)

    public void loadScores(int page) {
        try {
            this.currentPage = page;
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            Page<ScoreDTO> scorePage = scoreService.getAllScores(pageable);
            
            listPanel.loadData(scorePage.getContent());
        } catch (Exception e) {
            showError("Lỗi tải danh sách điểm: " + e.getMessage());
        }
    }

    public void addScore() {
        String cccd = inputCCCD();
        if (cccd == null) return;

        ScoreDTO score = new ScoreDTO();
        score.setCccd(cccd);

        ScoreFormDialog dialog = new ScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel), "Thêm Điểm Thi", score
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                scoreService.createScore(dialog.getScore());
                loadScores(currentPage);
                showInfo("Thêm điểm thi thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    public void editScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn dòng điểm thi cần sửa!");
            return;
        }

        ScoreFormDialog dialog = new ScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel), "Sửa Điểm Thi", selected
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                scoreService.updateScore(dialog.getScore());
                loadScores(currentPage);
                showInfo("Cập nhật điểm thi thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    public void deleteScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn dòng điểm thi cần xóa!");
            return;
        }

        if (confirmDelete("điểm thi của CCCD: " + selected.getCccd())) {
            try {
                scoreService.deleteScore(selected.getCccd());
                validatePageAfterDelete(true);
                showInfo("Xóa điểm thi thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    // 2. QUẢN LÝ ĐIỂM CỘNG (BONUS SCORE)

    public void loadBonusScores(int page) {
        try {
            this.currentPage = page;
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            Page<BonusScoreDTO> bonusPage = bonusScoreService.getAllBonusScores(pageable);
            
            // Giả sử listPanel có phương thức hiển thị dữ liệu Bonus
            // listPanel.loadBonusData(bonusPage.getContent());
        } catch (Exception e) {
            showError("Lỗi tải danh sách điểm ưu tiên: " + e.getMessage());
        }
    }

    public void addBonusScore() {
        String cccd = inputCCCD();
        if (cccd == null) return;

        BonusScoreDTO bonus = new BonusScoreDTO();
        bonus.setCccd(cccd);

        // Sử dụng Dialog dành riêng cho Điểm cộng
        BonusScoreFormDialog dialog = new BonusScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel), "Thêm Điểm Ưu Tiên", bonus
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                bonusScoreService.createBonusScore(dialog.getBonusScore());
                loadBonusScores(currentPage);
                showInfo("Thêm điểm ưu tiên thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    public void editBonusScore() {
        // Giả sử listPanel có phương thức lấy Bonus đang chọn
        BonusScoreDTO selected = listPanel.getSelectedBonusScore(); 
        if (selected == null) {
            showWarning("Vui lòng chọn dòng điểm ưu tiên cần sửa!");
            return;
        }

        BonusScoreFormDialog dialog = new BonusScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(scorePanel), "Sửa Điểm Ưu Tiên", selected
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                bonusScoreService.updateBonusScore(dialog.getBonusScore());
                loadBonusScores(currentPage);
                showInfo("Cập nhật điểm ưu tiên thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    public void deleteBonusScore() {
        BonusScoreDTO selected = listPanel.getSelectedBonusScore();
        if (selected == null) return;

        if (confirmDelete("điểm ưu tiên của CCCD: " + selected.getCccd())) {
            try {
                bonusScoreService.deleteBonusScore(selected.getCccd());
                validatePageAfterDelete(false);
                showInfo("Xóa điểm ưu tiên thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    // CÁC TIỆN ÍCH HỖ TRỢ (HELPERS)

    public void nextPage() {
        currentPage++;
        loadScores(currentPage); 
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadScores(currentPage);
        }
    }

    private void validatePageAfterDelete(boolean isScore) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        boolean isEmpty = isScore 
            ? scoreService.getAllScores(pageable).getContent().isEmpty()
            : bonusScoreService.getAllBonusScores(pageable).getContent().isEmpty();

        if (isEmpty && currentPage > 0) {
            currentPage--;
        }

        if (isScore) loadScores(currentPage);
        else loadBonusScores(currentPage);
    }

    private String inputCCCD() {
        String res = JOptionPane.showInputDialog(scorePanel, "Nhập CCCD của thí sinh:", "Xác nhận", JOptionPane.QUESTION_MESSAGE);
        return (res == null || res.trim().isEmpty()) ? null : res.trim();
    }

    private boolean confirmDelete(String message) {
        return JOptionPane.showConfirmDialog(scorePanel, "Bạn có chắc chắn muốn xóa " + message + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(scorePanel, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(scorePanel, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(scorePanel, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}