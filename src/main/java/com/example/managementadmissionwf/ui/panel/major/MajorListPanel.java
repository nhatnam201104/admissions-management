package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import com.example.managementadmissionwf.ui.util.UIFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MajorListPanel extends JPanel {

    private JTable masterTable, detailTable;
    private DefaultTableModel masterModel, detailModel;
    private final MajorController controller;

    public MajorListPanel(MajorController controller) {
        this.controller = controller;
        this.controller.setView(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        String[] mCols = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Điểm sàn", "Phương thức"};
        masterModel = new DefaultTableModel(mCols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        masterTable = UIFactory.createStandardTable(masterModel);
        masterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && masterTable.getSelectedRow() != -1) {
                controller.refreshDetail((String) masterTable.getValueAt(masterTable.getSelectedRow(), 0), detailModel);
            }
        });

        String[] dCols = {"Tổ hợp", "Môn 1", "Môn 2", "Môn 3"};
        detailModel = new DefaultTableModel(dCols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        detailTable = UIFactory.createStandardTable(detailModel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, UIFactory.createStandardScrollPane(masterTable), UIFactory.createStandardScrollPane(detailTable));
        split.setDividerLocation(340);
        add(split, BorderLayout.CENTER);
    }

    public void refreshData() {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel)) p = p.getParent();
        if (p instanceof AbstractFeaturePanel) ((AbstractFeaturePanel) p).refreshData();
    }

    public void updatePagination(Paging<?> paging) {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel)) p = p.getParent();
        if (p instanceof AbstractFeaturePanel) ((AbstractFeaturePanel) p).updatePagination(paging);
    }

    public String getSelectedMaNganh() {
        int r = masterTable.getSelectedRow();
        return (r >= 0) ? (String) masterTable.getValueAt(r, 0) : null;
    }

    public DefaultTableModel getMasterModel() { return masterModel; }
    public DefaultTableModel getDetailModel() { return detailModel; }
}