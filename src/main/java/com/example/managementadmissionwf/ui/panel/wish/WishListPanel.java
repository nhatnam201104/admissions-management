package com.example.managementadmissionwf.ui.panel.wish;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import com.example.managementadmissionwf.dto.wish.WishDTO;

public class WishListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private List<WishDTO> fullData = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 15;

    private JTextField txtPageInput;
    private JLabel lblTotalPages;
    private JButton btnPrev, btnNext;

    public WishListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 2, 20));

        initTable();
        add(createPaginationPanel(), BorderLayout.SOUTH);

        initMockData();
        updateDisplay();
    }

    private void initTable() {
        String[] cols = {"Mã NV", "CCCD", "Họ tên", "Thứ tự", "Mã ngành", "Tên ngành", "Điểm XT", "ƯT", "Cộng", "Tổng", "Kết quả"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    comp.setForeground(Color.BLACK);  // chữ đen khi selected
                } else {
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        };

        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(245, 245, 245));
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setPreferredScrollableViewportSize(null);
        
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    if (value != null) {
                        String kq = value.toString();
                        switch (kq) {
                            case "TRUNG_TUYEN":
                                c.setBackground(new Color(46, 204, 113));
                                c.setForeground(Color.WHITE);
                                break;
                            case "TRUOT":
                                c.setBackground(new Color(231, 76, 60));
                                c.setForeground(Color.WHITE);
                                break;
                            case "CHO_XET":
                                c.setBackground(new Color(241, 196, 15));
                                c.setForeground(Color.BLACK);
                                break;
                            default:
                                c.setBackground(Color.WHITE);
                                c.setForeground(Color.BLACK);
                        }
                    }
                }
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(252, 252, 252));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(100, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 1)); 
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setViewportBorder(null);  
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPaginationPanel() {
        JPanel pnlPaging = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        pnlPaging.setBackground(Color.WHITE);

        btnPrev = createNavButton("<");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateDisplay();
            }
        });

        txtPageInput = new JTextField(String.valueOf(currentPage));
        txtPageInput.setPreferredSize(new Dimension(40, 30));
        txtPageInput.setHorizontalAlignment(JTextField.CENTER);
        txtPageInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtPageInput.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        txtPageInput.addActionListener(e -> {
            try {
                int target = Integer.parseInt(txtPageInput.getText());
                int totalPages = getTotalPages();
                if (target >= 1 && target <= totalPages) {
                    currentPage = target;
                } else {
                    txtPageInput.setText(String.valueOf(currentPage));
                }
                updateDisplay();
            } catch (NumberFormatException ex) {
                txtPageInput.setText(String.valueOf(currentPage));
            }
        });

        lblTotalPages = new JLabel("/ 1");
        lblTotalPages.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnNext = createNavButton(">");
        btnNext.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                updateDisplay();
            }
        });

        pnlPaging.add(new JLabel("Trang:"));
        pnlPaging.add(btnPrev);
        pnlPaging.add(txtPageInput);
        pnlPaging.add(lblTotalPages);
        pnlPaging.add(btnNext);

        return pnlPaging;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(35, 30));
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    private void updateDisplay() {
        model.setRowCount(0);
        int total = fullData.size();
        int totalPages = getTotalPages();

        if (total > 0) {
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            for (int i = start; i < end; i++) {
                WishDTO w = fullData.get(i);
                model.addRow(new Object[]{
                    w.getId(), w.getNnCccd(), w.getHoTenThiSinh(), w.getNvTt(),
                    w.getNvManganh(), w.getTenNganh(), w.getDiemThxt(), w.getDiemUtqd(),
                    w.getDiemCong(), w.getDiemXettuyen(), w.getNvKetqua()
                });
            }
        }

        txtPageInput.setText(String.valueOf(currentPage));
        lblTotalPages.setText("/ " + Math.max(1, totalPages));

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) fullData.size() / pageSize);
    }

    private void initMockData() {
        fullData.clear();
        String[] maNganh = {"7480201", "7480103", "7480104", "7480202"};
        String[] tenNganh = {"Công nghệ thông tin", "Kỹ thuật phần mềm", "Hệ thống thông tin", "An toàn thông tin"};
        String[] ketQua = {"TRUNG_TUYEN", "TRUOT", "CHO_XET"};

        for (int i = 1; i <= 45; i++) {
            WishDTO w = new WishDTO();
            w.setId(i);
            w.setNnCccd("07920600" + (1000 + i));
            w.setHoTenThiSinh("Thí sinh " + i);
            int idx = i % 4;
            w.setNvManganh(maNganh[idx]);
            w.setTenNganh(tenNganh[idx]);
            w.setNvTt((i % 10) + 1);
            w.setDiemThxt(24.5 + (i % 5) * 0.5);
            w.setDiemUtqd(0.0 + (i % 3) * 0.5);
            w.setDiemCong(0.0 + (i % 4) * 0.25);
            w.setDiemXettuyen(w.getDiemThxt() + w.getDiemUtqd() + w.getDiemCong());
            w.setNvKetqua(ketQua[i % 3]);
            fullData.add(w);
        }
    }

    public void setData(List<WishDTO> list) {
        this.fullData = new ArrayList<>(list);
        this.currentPage = 1;
        updateDisplay();
    }

    public WishDTO getSelectedWish() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelIndex = (currentPage - 1) * pageSize + viewRow;
        if (modelIndex >= 0 && modelIndex < fullData.size()) {
            return fullData.get(modelIndex);
        }
        return null;
    }

    public void addWishMock(WishDTO dto) {
        if (dto.getId() == null) {
            dto.setId(fullData.isEmpty() ? 1 : fullData.get(fullData.size() - 1).getId() + 1);
        }
        fullData.add(dto);
        currentPage = getTotalPages();
        updateDisplay();
    }

    public void updateWishMock(WishDTO dto) {
        for (int i = 0; i < fullData.size(); i++) {
            if (fullData.get(i).getId().equals(dto.getId())) {
                fullData.set(i, dto);
                break;
            }
        }
        updateDisplay();
    }

    public void deleteWishMock(Integer id) {
        fullData.removeIf(w -> w.getId().equals(id));
        if (currentPage > getTotalPages() && getTotalPages() > 0) {
            currentPage = getTotalPages();
        }
        updateDisplay();
    }

    public JTable getTable() {
        return table;
    }

    public List<WishDTO> getAllData() {
        return new ArrayList<>(fullData);
    }
}