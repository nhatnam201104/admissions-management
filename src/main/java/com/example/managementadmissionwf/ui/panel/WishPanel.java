//package com.example.managementadmissionwf.ui.panel;
//
//import javax.annotation.PostConstruct;
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.util.List;
//
//import com.example.managementadmissionwf.dto.WishDTO;
//import com.example.managementadmissionwf.bus.interfaces.WishService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class WishPanel extends JPanel {
//
//    @Autowired
//    private WishService wishService; // inject từ Spring
//
//    private JTextField txtSearch;
//    private JComboBox<String> cbResult;
//    private JButton btnSearch, btnAdd, btnEdit, btnDelete, btnCalc, btnUpdate, btnExport;
//
//    private WishListPanel wishListPanel;
//
//    public WishPanel() {
//        initUI();
//    }
//
//    @PostConstruct
//    public void initData() {
//        loadAllWishes();
//    }
//
//    private void initUI() {
//        setLayout(new BorderLayout());
//        setBackground(Color.WHITE);
//        setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        add(createFilterPanel(), BorderLayout.NORTH);
//        add(createCenterPanel(), BorderLayout.CENTER);
//    }
//
//    // ================= FILTER PANEL =================
//    private JPanel createFilterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(5, 5, 10, 5));
//
//        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
//        left.setBackground(Color.WHITE);
//
//        txtSearch = new JTextField(20);
//        txtSearch.setPreferredSize(new Dimension(250, 35));
//        txtSearch.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
//                new EmptyBorder(5, 10, 5, 10)
//        ));
//        txtSearch.setToolTipText("Nhập CCCD / Tên / SBD...");
//
//        cbResult = new JComboBox<>(new String[]{
//                "Tất cả", "TRUNG_TUYEN", "TRUOT", "CHO_XET"
//        });
//        cbResult.setPreferredSize(new Dimension(150, 35));
//
//        btnSearch = createButton("Tìm kiếm", new Color(33, 150, 243));
//
//        left.add(txtSearch);
//        left.add(cbResult);
//        left.add(btnSearch);
//
//        panel.add(left, BorderLayout.WEST);
//
//        // Action event
//        btnSearch.addActionListener(e -> searchWishes());
//
//        return panel;
//    }
//
//    // ================= CENTER =================
//    private JPanel createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(Color.WHITE);
//
//        panel.add(createActionPanel(), BorderLayout.NORTH);
//
//        wishListPanel = new WishListPanel();
//        panel.add(wishListPanel, BorderLayout.CENTER);
//
//        return panel;
//    }
//
//    // ================= ACTION PANEL =================
//    private JPanel createActionPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        panel.setBackground(Color.WHITE);
//
//        btnAdd = createButton("Thêm", new Color(76, 175, 80));
//        btnEdit = createButton("Sửa", new Color(255, 152, 0));
//        btnDelete = createButton("Xóa", new Color(244, 67, 54));
//        btnCalc = createButton("Tính điểm", new Color(33, 150, 243));
//        btnUpdate = createButton("Cập nhật KQ", new Color(0, 150, 136));
//        btnExport = createButton("Export", new Color(121, 85, 72));
//
//        panel.add(btnAdd);
//        panel.add(btnEdit);
//        panel.add(btnDelete);
//        panel.add(btnCalc);
//        panel.add(btnUpdate);
//        panel.add(btnExport);
//
//        // Actions
//        btnAdd.addActionListener(e -> openForm(null));
//        btnEdit.addActionListener(e -> editWish());
//        btnDelete.addActionListener(e -> deleteWish());
//        btnCalc.addActionListener(e -> calculateScore());
//        btnUpdate.addActionListener(e -> updateResult());
//        btnExport.addActionListener(e -> exportData());
//
//        return panel;
//    }
//
//    // ================= STYLE BUTTON =================
//    private JButton createButton(String text, Color color) {
//        JButton btn = new JButton(text);
//        btn.setFocusPainted(false);
//        btn.setForeground(Color.WHITE);
//        btn.setBackground(color);
//        btn.setPreferredSize(new Dimension(130, 35));
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        return btn;
//    }
//
//    // ================= LOGIC =================
//
//    private void loadAllWishes() {
//        List<WishDTO> list = wishService.getAllWishes();
//        wishListPanel.setData(list);
//    }
//
//    private void searchWishes() {
//        String keyword = txtSearch.getText();
//        String result = cbResult.getSelectedItem().toString();
//
//        if (result.equals("Tất cả")) result = null;
//
//        List<WishDTO> list = wishService.searchWishes(keyword, result);
//        wishListPanel.setData(list);
//    }
//
//    private void openForm(WishDTO dto) {
//        Window window = SwingUtilities.getWindowAncestor(this);
//        WishFormDialog dialog = new WishFormDialog(window, dto);
//        dialog.setVisible(true);
//
//        loadAllWishes();
//    }
//
//    private void editWish() {
//        WishDTO selected = wishListPanel.getSelected();
//        if (selected == null) {
//            showMessage("Vui lòng chọn dòng!");
//            return;
//        }
//        openForm(selected);
//    }
//
//    private void deleteWish() {
//        WishDTO selected = wishListPanel.getSelected();
//        if (selected == null) {
//            showMessage("Chưa chọn dữ liệu!");
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "Xóa nguyện vọng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            wishService.deleteWish(selected.getId());
//            loadAllWishes();
//        }
//    }
//
//    private void calculateScore() {
//        List<WishDTO> list = wishListPanel.getAll();
//
//        for (WishDTO w : list) {
//            wishService.calculateScore(w);
//        }
//
//        loadAllWishes();
//    }
//
//    private void updateResult() {
//        WishDTO selected = wishListPanel.getSelected();
//        if (selected == null) {
//            showMessage("Chọn dòng để cập nhật!");
//            return;
//        }
//
//        String[] options = {"TRUNG_TUYEN", "TRUOT", "CHO_XET"};
//        String result = (String) JOptionPane.showInputDialog(
//                this,
//                "Chọn kết quả:",
//                "Cập nhật",
//                JOptionPane.PLAIN_MESSAGE,
//                null,
//                options,
//                selected.getNvKetqua()
//        );
//
//        if (result != null) {
//            wishService.updateResult(selected.getId(), result);
//            loadAllWishes();
//        }
//    }
//
//    private void exportData() {
//        showMessage("Chức năng Export đang phát triển...");
//    }
//
//    private void showMessage(String msg) {
//        JOptionPane.showMessageDialog(this, msg);
//    }
//}