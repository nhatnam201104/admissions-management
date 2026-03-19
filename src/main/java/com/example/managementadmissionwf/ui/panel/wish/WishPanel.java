package com.example.managementadmissionwf.ui.panel.wish;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.dto.wish.WishDTO;

@Component
public class WishPanel extends JPanel {

    @Autowired
    private WishController controller;

    private WishListPanel listPanel = new WishListPanel();

    private JTextField txtSearch = new JTextField();
    private JComboBox<String> cbFilter = new JComboBox<>(new String[]{"Tất cả", "TRUNG_TUYEN", "TRUOT", "CHO_XET"});

    private final String PLACEHOLDER = "Nhập nội dung tìm kiếm...";

    public WishPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
    }

    @jakarta.annotation.PostConstruct
    private void init() {
        loadData();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        // LEFT: ACTION BUTTONS
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        left.setBackground(Color.WHITE);

        JButton btnAdd    = createFlatActionButton("THÊM", "icon-add.png", new Color(46, 204, 113));
        JButton btnEdit   = createFlatActionButton("SỬA", "icon-edit.png", new Color(241, 196, 15));
        JButton btnDelete = createFlatActionButton("XÓA", "icon-delete.png", new Color(231, 76, 60));
        JButton btnExport = createFlatActionButton("XUẤT EXCEL", "icon-excel.png", new Color(39, 174, 96));

        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnExport.addActionListener(e -> exportExcel());

        left.add(btnAdd);
        left.add(btnEdit);
        left.add(btnDelete);
        left.add(btnExport);

        // RIGHT: SEARCH & FILTER
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 30));
        right.setBackground(Color.WHITE);

        cbFilter.setPreferredSize(new Dimension(140, 34));
        cbFilter.setMaximumRowCount(6);
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter.setBackground(Color.WHITE);
        cbFilter.setFocusable(false);
        cbFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        txtSearch.setText(PLACEHOLDER);
        txtSearch.setForeground(new Color(150, 150, 150));
        txtSearch.setPreferredSize(new Dimension(220, 32));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals(PLACEHOLDER)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
                txtSearch.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(26, 188, 156)),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(new Color(150, 150, 150));
                    txtSearch.setText(PLACEHOLDER);
                }
                txtSearch.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 210)),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }
        });

        // Nút Làm mới - có viền từ đầu, hover thay đổi rõ ràng
        JButton btnRefresh = new JButton("Làm mới", getIcon("icon-reload.png", 18, 18));
        btnRefresh.setPreferredSize(new Dimension(110, 34));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setForeground(new Color(50, 50, 50));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setFocusable(false);
        btnRefresh.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));  // viền xám nhạt từ đầu
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setOpaque(true);
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRefresh.setBackground(new Color(235, 245, 245));     // nền xanh nhạt
                btnRefresh.setBorder(BorderFactory.createLineBorder(new Color(26, 188, 156), 1));  // viền teal
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRefresh.setBackground(Color.WHITE);
                btnRefresh.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));  // quay về viền xám
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText(PLACEHOLDER);
            txtSearch.setForeground(new Color(150, 150, 150));
            cbFilter.setSelectedIndex(0);
            loadData();
        });

        right.add(cbFilter);
        right.add(txtSearch);
        right.add(btnRefresh);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private void openAddDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        WishFormDialog dialog = new WishFormDialog(parent, "Thêm nguyện vọng mới", null);
        dialog.setVisible(true);
        WishDTO result = dialog.getResult();
        if (result != null) {
            if (controller != null) {
                controller.addWish(result);
            } else {
                listPanel.addWishMock(result);
            }
            loadData();
        }
    }

    private void openEditDialog() {
        WishDTO selected = listPanel.getSelectedWish();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nguyện vọng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        WishFormDialog dialog = new WishFormDialog(parent, "Sửa nguyện vọng", selected);
        dialog.setVisible(true);
        WishDTO result = dialog.getResult();
        if (result != null) {
            if (controller != null) {
                controller.editWish(result);
            } else {
                listPanel.updateWishMock(result);
            }
            loadData();
        }
    }

    private void deleteSelected() {
        WishDTO selected = listPanel.getSelectedWish();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nguyện vọng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa nguyện vọng ID " + selected.getId() + " không?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (controller != null) {
                controller.deleteWish(selected.getId());
            } else {
                listPanel.deleteWishMock(selected.getId());
            }
            loadData();
        }
    }

    private void exportExcel() {
        JOptionPane.showMessageDialog(this,
            "Đã xuất file Excel thành công!\n(Tạm thời mock - chứa " + listPanel.getAllData().size() + " nguyện vọng)",
            "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadData() {
        String keyword = txtSearch.getText().equals(PLACEHOLDER) ? "" : txtSearch.getText().trim();
        String result = cbFilter.getSelectedItem().toString();

        if (controller != null) {
            listPanel.setData(controller.searchWishes(keyword, result));
        } else {
            listPanel.setData(listPanel.getAllData());
        }
    }

    private JButton createFlatActionButton(String text, String iconName, Color activeColor) {
        JButton btn = new JButton(text);
        btn.setIcon(getIcon(iconName, 42, 42));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(80, 80, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(activeColor);
                btn.setOpaque(true);
                btn.setBackground(new Color(250, 250, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(80, 80, 80));
                btn.setOpaque(false);
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    private ImageIcon getIcon(String name, int width, int height) {
        try {
            URL imgURL = getClass().getResource("/icons/" + name);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = resized.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(img, 0, 0, width, height, null);
                g2.dispose();
                return new ImageIcon(resized);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("ComboBox.focus", new Color(0, 0, 0, 0));
            UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        } catch (Exception e) {}

        JFrame frame = new JFrame("Wish Management Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 750);
        frame.setContentPane(new WishPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}