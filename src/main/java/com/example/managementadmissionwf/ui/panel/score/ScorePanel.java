package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

@Component
public class ScorePanel extends AbstractFeaturePanel {

    @Autowired
    private ScoreController controller;
    
    private ScoreListPanel listPanel;
    private JComboBox<String> cboPhuongThuc;
    
    private int currentPage = 0;
    private final int pageSize = 15;

    public ScorePanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new ScoreListPanel();
        buildUI();
    }

    @Override
    protected void onInit() {
        // Không cần gọi controller.setPanel vì Controller không còn điều khiển View trực tiếp
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"}, 100);
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void loadData() {
        try {
            String keyword = getSearchField().getText().trim();
            String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
            
            Page<ScoreDTO> page = controller.getScores(keyword, phuongThuc, currentPage, pageSize);
            
            listPanel.loadData(page.getContent());
            super.updatePaginationDirect(currentPage + 1, page.getTotalPages(), page.getTotalElements());
        } catch (Exception e) {
        e.printStackTrace(); 
        String detailedError = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
        JOptionPane.showMessageDialog(this, "Lỗi cụ thể: " + detailedError, "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
    }
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> handleAdd();
            case EDIT -> handleEdit();
            case DELETE -> handleDelete();
            case REFRESH -> {
                currentPage = 0;
                loadData();
            }
            case EXPORT_EXCEL -> showInfo("Chức năng xuất Excel đang thực hiện...");
            default -> {}
        }
    }

    private void handleAdd() {
        String cccd = inputCCCD();
        if (cccd == null) return;

        ScoreDTO score = new ScoreDTO();
        score.setCccd(cccd);

        ScoreFormDialog dialog = new ScoreFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Điểm Thi", score);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                controller.saveScore(dialog.getScore(), false);
                loadData();
                showInfo("Thêm thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    private void handleEdit() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn dòng cần sửa!");
            return;
        }

        ScoreFormDialog dialog = new ScoreFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Điểm Thi", selected);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                controller.saveScore(dialog.getScore(), true);
                loadData();
                showInfo("Cập nhật thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    private void handleDelete() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa điểm của CCCD: " + selected.getCccd() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteScore(selected.getCccd());
                loadData();
                showInfo("Xóa thành công!");
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    // UTILS HIỂN THỊ - Chỉ View mới có các hàm này
    private String inputCCCD() {
        return JOptionPane.showInputDialog(this, "Nhập CCCD:");
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    protected JComponent createContentPanel() { return listPanel; }

    @Override
    protected String getItemLabel() { return "bản ghi"; }

    public ScoreListPanel getListPanel() { return listPanel; }
}