package com.example.managementadmissionwf.ui.panel.subjectgroup;

import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog xem chi tiết một tổ hợp môn (read-only).
 * Bổ sung theo rubric review (SubjectGroup detail dialog).
 */
public class SubjectGroupDetailDialog extends JDialog {

    public SubjectGroupDetailDialog(Frame parent, SubjectGroupResponse data) {
        super(parent, "Chi tiết tổ hợp môn - " + safe(data.getMatohop()), true);
        setSize(420, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel info = new JPanel(new GridBagLayout());
        info.setBorder(BorderFactory.createTitledBorder("Thông tin tổ hợp"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(info, gbc, 0, "Mã tổ hợp", safe(data.getMatohop()));
        addRow(info, gbc, 1, "Tên tổ hợp", safe(data.getTentohop()));
        addRow(info, gbc, 2, "Môn 1", safe(data.getMon1()));
        addRow(info, gbc, 3, "Môn 2", safe(data.getMon2()));
        addRow(info, gbc, 4, "Môn 3", safe(data.getMon3()));

        add(info, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(closeBtn);
        add(actions, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(new JLabel(value != null && !value.isBlank() ? value : "-"), gbc);
    }

    private static String safe(String s) {
        return s != null ? s : "-";
    }
}
