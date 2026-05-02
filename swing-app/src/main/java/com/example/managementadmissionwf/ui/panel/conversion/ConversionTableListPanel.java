package com.example.managementadmissionwf.ui.panel.conversion;

import com.example.managementadmissionwf.dto.ConversionTableDTO;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConversionTableListPanel extends JPanel {

    private JTable masterTable;
    private DefaultTableModel masterModel;
    private final ConversionTableController controller;

    public ConversionTableListPanel(ConversionTableController controller) {
        this.controller = controller;
        this.controller.setView(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        // Thêm ID column làm cột đầu tiên (hidden)
        String[] mCols = { "ID", "STT", "Phương thức", "Tổ hợp", "Môn", "Điểm A", "Điểm B", "Điểm C", "Điểm D" };
        masterModel = new DefaultTableModel(mCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        masterTable = UIFactory.createStandardTable(masterModel);

        // Ẩn cột ID (column 0)
        masterTable.getColumnModel().getColumn(0).setMinWidth(0);
        masterTable.getColumnModel().getColumn(0).setMaxWidth(0);
        masterTable.getColumnModel().getColumn(0).setWidth(0);

        add(UIFactory.createStandardScrollPane(masterTable), BorderLayout.CENTER);
    }

    public void refreshData() {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel))
            p = p.getParent();
        if (p instanceof AbstractFeaturePanel) {
            ((AbstractFeaturePanel) p).refreshData();
        }
    }

    public void updatePagination(Paging<?> paging) {
        Container p = getParent();
        while (p != null && !(p instanceof AbstractFeaturePanel))
            p = p.getParent();
        if (p instanceof AbstractFeaturePanel) {
            ((AbstractFeaturePanel) p).updatePagination(paging);
        }
    }

    public Integer getSelectedId() {
        int r = masterTable.getSelectedRow();
        if (r < 0)
            return null;
        // Column 0 = ID (hidden column)
        Object value = masterTable.getValueAt(r, 0);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    public DefaultTableModel getMasterModel() {
        return masterModel;
    }

    public JTable getMasterTable() {
        return masterTable;
    }

    public void loadData(List<ConversionTableDTO> data) {
        masterModel.setRowCount(0);
        int stt = 1;
        for (ConversionTableDTO dto : data) {
            masterModel.addRow(new Object[] {
                    dto.getId(), // Column 0 - ID (hidden)
                    stt++, // Column 1 - STT
                    dto.getPhuongThuc(), // Column 2 - Phương thức
                    dto.getToHop() != null ? dto.getToHop() : "-", // Column 3 - Tổ hợp
                    dto.getMon(), // Column 4 - Môn
                    dto.getDiemA(), // Column 5 - Điểm A
                    dto.getDiemB(), // Column 6 - Điểm B
                    dto.getDiemC(), // Column 7 - Điểm C
                    dto.getDiemD() // Column 8 - Điểm D
            });
        }
    }
}