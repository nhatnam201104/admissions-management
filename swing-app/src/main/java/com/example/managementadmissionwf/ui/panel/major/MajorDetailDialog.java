package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog xem chi tiết 1 ngành: hiển thị thông tin chính + danh sách tổ hợp môn.
 * Bổ sung theo rubric review (Major detail dialog).
 */
public class MajorDetailDialog extends JDialog {

    public MajorDetailDialog(Frame parent, MajorDTO dto) {
        super(parent, "Chi tiết ngành - " + safe(dto.getMaNganh()), true);
        setSize(720, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildInfoPanel(dto), BorderLayout.NORTH);
        add(buildTohopTable(dto.getTohopList()), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(closeBtn);
        add(actions, BorderLayout.SOUTH);
    }

    private JPanel buildInfoPanel(MajorDTO dto) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin ngành"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addRow(panel, gbc, 0, "Mã ngành", safe(dto.getMaNganh()));
        addRow(panel, gbc, 1, "Tên ngành", safe(dto.getTenNganh()));
        addRow(panel, gbc, 2, "Tổ hợp gốc", safe(dto.getTohopGoc()));
        addRow(panel, gbc, 3, "Chỉ tiêu", String.valueOf(dto.getChiTieu() != null ? dto.getChiTieu() : 0));
        addRow(panel, gbc, 4, "Điểm sàn", formatScore(dto.getDiemSan()));
        addRow(panel, gbc, 5, "Điểm chuẩn", formatScore(dto.getDiemTrungTuyen()));

        addRow(panel, gbc, 6, "Phương thức", buildPhuongThucString(dto));
        addRow(panel, gbc, 7, "Số NV đăng ký",
                String.valueOf(dto.getTotalAspirations() != null ? dto.getTotalAspirations() : 0));
        return panel;
    }

    private String buildPhuongThucString(MajorDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(dto.getThpt())) sb.append("THPT (").append(safeInt(dto.getSlThpt())).append(") ");
        if (Boolean.TRUE.equals(dto.getDgnl())) sb.append("DGNL (").append(safeInt(dto.getSlDgnl())).append(") ");
        if (Boolean.TRUE.equals(dto.getVsat())) sb.append("VSAT (").append(safeInt(dto.getSlVsat())).append(") ");
        if (Boolean.TRUE.equals(dto.getTuyenThang())) sb.append("Tuyển thẳng (").append(safeInt(dto.getSlXtt())).append(")");
        return sb.length() == 0 ? "-" : sb.toString().trim();
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

    private JComponent buildTohopTable(List<MajorTohopDTO> tohopList) {
        String[] cols = {"Mã tổ hợp", "Môn 1", "Hệ số 1", "Môn 2", "Hệ số 2", "Môn 3", "Hệ số 3"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (tohopList != null) {
            for (MajorTohopDTO t : tohopList) {
                model.addRow(new Object[]{
                        safe(t.getMaToHop()),
                        safe(t.getThMon1()), formatScore(t.getHsMon1()),
                        safe(t.getThMon2()), formatScore(t.getHsMon2()),
                        safe(t.getThMon3()), formatScore(t.getHsMon3())
                });
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Tổ hợp môn xét tuyển"));
        return sp;
    }

    private static String safe(String s) {
        return s != null ? s : "-";
    }

    private static int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private static String formatScore(Double value) {
        return value != null ? String.format("%.2f", value) : "-";
    }
}
