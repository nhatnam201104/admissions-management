package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import com.example.managementadmissionwf.ui.util.UIFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MajorListPanel extends JPanel {

    private JTable masterTable;
    private DefaultTableModel masterModel;
    private final MajorController controller;

    public MajorListPanel(MajorController controller) {
        this.controller = controller;
        this.controller.setView(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        String[] mCols = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Điểm sàn",
                "Điểm chuẩn", "Số NV", "Phương thức"};
        masterModel = new DefaultTableModel(mCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        masterTable = UIFactory.createStandardTable(masterModel);
        // Không còn selection listener vì đã xóa bảng chi tiết

        add(UIFactory.createStandardScrollPane(masterTable), BorderLayout.CENTER);
    }

    public void refreshData() {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel)) p = p.getParent();
        if (p instanceof AbstractFeaturePanel) {
            ((AbstractFeaturePanel) p).refreshData();
        }
    }

    public void updatePagination(Paging<?> paging) {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel)) p = p.getParent();
        if (p instanceof AbstractFeaturePanel) {
            ((AbstractFeaturePanel) p).updatePagination(paging);
        }
    }

    public String getSelectedMaNganh() {
        int r = masterTable.getSelectedRow();
        return (r >= 0) ? (String) masterTable.getValueAt(r, 0) : null;
    }

    public DefaultTableModel getMasterModel() {
        return masterModel;
    }
}
