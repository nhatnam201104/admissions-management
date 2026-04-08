# Tài liệu: Luồng xây dựng UI - Admissions Management System

## 1. Tổng quan kiến trúc UI

Ứng dụng sử dụng **Java Swing** làm framework UI desktop, chạy trên nền **Spring Boot 4.0.2**.
Kiến trúc tuân theo mô hình **MVC (Model-View-Controller)** kết hợp **Dependency Injection** của Spring.

```
┌─────────────────────────────────────────────────────────┐
│                  ManagementAdmissionWfApplication        │
│                    (Spring Boot Entry Point)              │
└──────────────────────┬──────────────────────────────────┘
                       │ new LoginFrame()
                       ▼
┌─────────────────────────────────────────────────────────┐
│                      LoginFrame                          │
│  ┌──────────────────┐  ┌────────────────────────────┐   │
│  │ LeftWelcomePanel │  │     RightLoginPanel        │   │
│  │  (Welcome info)  │  │  (Login form + Auth)       │   │
│  └──────────────────┘  └─────────────┬──────────────┘   │
└──────────────────────────────────────┼──────────────────┘
                                       │ login success
                                       ▼
┌─────────────────────────────────────────────────────────┐
│                       MainFrame                          │
│  ┌──────────┐  ┌────────────────────────────────────┐  │
│  │ Sidebar  │  │         Content Panel              │  │
│  │ (Menu)   │  │  (CandidatePanel, ScorePanel, ...) │  │
│  └──────────┘  └────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Chuỗi khởi động ứng dụng

### 2.1. Entry Point — `ManagementAdmissionWfApplication`

**File:** [ManagementAdmissionWfApplication.java](../../src/main/java/com/example/managementadmissionwf/ManagementAdmissionWfApplication.java)

```java
@SpringBootApplication
public class ManagementAdmissionWfApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false"); // Bắt buộc cho Swing
        SpringApplication.run(ManagementAdmissionWfApplication.class, args);
        LoginFrame loginFrame = new LoginFrame();
        Thread.setDefaultUncaughtExceptionHandler(new GlobalException());
        SwingUtilities.invokeLater(loginFrame::display);
    }
}
```

**Luồng:**
1. Tắt chế độ headless (cho phép hiển thị GUI)
2. Khởi động Spring Context (tạo tất cả beans, kết nối DB, seed data...)
3. Tạo `LoginFrame` (chuẩn bị UI đăng nhập)
4. Cài global exception handler
5. Hiển thị LoginFrame trên EDT (Event Dispatch Thread)

### 2.2. Spring Context Holders — `ApplicationContextHolder`

**File:** [ApplicationContextHolder.java](../../src/main/java/com/example/managementadmissionwf/config/ApplicationContextHolder.java)

```java
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
```

**Mục đích:** Cung cấp truy cập static đến Spring beans cho các UI component không được Spring quản lý trực tiếp (vd: `JFrame`, `JDialog` được tạo bằng `new`).

### 2.3. UI Bean Config — `UIBeanConfig`

**File:** [UIBeanConfig.java](../../src/main/java/com/example/managementadmissionwf/config/UIBeanConfig.java)

Đăng ký các UI panel như Spring beans để hỗ trợ dependency injection:

| Bean | Scope | Lý do |
|------|-------|-------|
| `MainFrame` | prototype | Mỗi lần login tạo frame mới |
| `CandidatePanel` | singleton | Chia sẻ trong suốt vòng đời app |
| `ScorePanel` | singleton | Tương tự |
| `MajorPanel` | singleton | Tương tự |
| `UserManagementPanel` | singleton | Tương tự |

---

## 3. Luồng xây dựng LoginFrame

### 3.1. Cấu trúc Layout

**File:** [LoginFrame.java](../../src/main/java/com/example/managementadmissionwf/ui/frame/LoginFrame.java)

```
┌────────────────────────────────────────────────┐
│                 LoginFrame (JFrame)            │
│  ┌──────────────────┬─────────────────────────┐│
│  │                  │                          ││
│  │  LeftWelcomePanel│   RightLoginPanel        ││
│  │                  │                          ││
│  │  - Gradient BG   │   - Username field       ││
│  │  - App logo      │   - Password field       ││
│  │  - Welcome text  │   - Login button         ││
│  │                  │   - Error message        ││
│  │                  │                          ││
│  └──────────────────┴─────────────────────────┘│
└────────────────────────────────────────────────┘
```

**Layout Manager:** `BorderLayout` — LeftPanel ở WEST, RightPanel ở CENTER.

### 3.2. Luồng xác thực (Authentication Flow)

```
User nhập username/password
       │
       ▼
RightLoginPanel.handleLogin()
       │
       ▼
ApplicationContextHolder.getBean(AuthService.class)
       │
       ▼
AuthService.login(username, password)
       │
       ├─ SUCCESS → UserDTO
       │     │
       │     ▼
       │   ApplicationContextHolder.getBean(MainFrame.class)
       │     │
       │     ▼
       │   new MainFrame(userDTO) → mainFrame.display()
       │     │
       │     ▼
       │   loginFrame.dispose() // Đóng login
       │
       └─ FAIL → JOptionPane.showMessageDialog(error)
```

---

## 4. Luồng xây dựng MainFrame

### 4.1. Cấu trúc Layout

**File:** [MainFrame.java](../../src/main/java/com/example/managementadmissionwf/ui/frame/MainFrame.java)

```
┌──────────────────────────────────────────────────────────┐
│                       MainFrame                          │
│  ┌────────────┐  ┌───────────────────────────────────┐  │
│  │  Sidebar   │  │         Content Panel             │  │
│  │            │  │  (đổi động qua setContent())       │  │
│  │ ┌────────┐ │  │                                   │  │
│  │ │ Avatar │ │  │  ┌─────────────────────────────┐  │  │
│  │ │  Info  │ │  │  │  AbstractFeaturePanel       │  │  │
│  │ └────────┘ │  │  │  ┌───────────────────────┐  │  │  │
│  │            │  │  │  │ Filter + Action Bar   │  │  │  │
│  │ ─────────  │  │  │  ├───────────────────────┤  │  │  │
│  │ [Thí sinh]│  │  │  │ Content Panel         │  │  │  │
│  │ [Điểm thi]│  │  │  │ (JTable / Custom)     │  │  │  │
│  │ [Ngành]   │  │  │  ├───────────────────────┤  │  │  │
│  │ [Tổ hợp]  │  │  │  │ Pagination Bar        │  │  │  │
│  │ [Người dùng]│ │  │  └───────────────────────┘  │  │  │
│  │ [Xét tuyển]│ │  │  └─────────────────────────────┘  │  │
│  │ [Quy đổi] │  │  │                                   │  │
│  │ [Điểm cộng]│ │  │                                   │  │
│  │ [Thống kê]│  │  │                                   │  │
│  │ [Đăng xuất]│ │  │                                   │  │
│  └────────────┘  └───────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

**Layout Manager:** `BorderLayout` — Sidebar ở WEST, Content ở CENTER.

### 4.2. Navigation Controller

**File:** [Navigation.java](../../src/main/java/com/example/managementadmissionwf/ui/component/Navigation.java)

Điều khiển việc chuyển đổi giữa các panel:

```java
public void init(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    setupMenuListeners();
    showCandidatePanel(); // Panel mặc định khi vào app
}
```

**Luồng chuyển panel:**
```
Click menu button
      │
      ▼
Navigation.showXxxPanel()
      │
      ▼
mainFrame.setContent(xxxPanel)
      │
      ▼
mainFrame.highlightMenuButton(selectedButton)
      │
      ▼
xxxPanel.onShow() // Panel tự load data khi hiển thị
```

### 4.3. Menu Items & Panels Mapping

| Menu Item | Panel Class | Controller Class |
|-----------|-------------|-----------------|
| Thí sinh | `CandidatePanel` | `CandidateController` |
| Điểm thi | `ScorePanel` | `ScoreController` |
| Ngành | `MajorPanel` | `MajorController` |
| Tổ hợp môn | `SubjectGroupPanel` | `SubjectGroupController` |
| Người dùng | `UserManagementPanel` | `UserController` |
| Xét tuyển | `AdmissionPanel` | `AdmissionResultController` |
| Bảng quy đổi | `ConversionTablePanel` | — |
| Điểm cộng | `BonusScorePanel` | — |
| Thống kê | `StatisticPanel` | — |

---

## 5. Hệ thống AbstractFeaturePanel — Template cho tất cả Panel

### 5.1. AbstractFeaturePanel

**File:** [AbstractFeaturePanel.java](../../src/main/java/com/example/managementadmissionwf/ui/panel/AbstractFeaturePanel.java)

Đây là **base class** cho tất cả feature panels, cung cấp layout chuẩn:

```
┌──────────────────────────────────────────────┐
│            AbstractFeaturePanel               │
│  ┌──────────────────────────────────────┐   │
│  │ Top Section (BorderLayout.NORTH)      │   │
│  │ ┌────────────────┬──────────────────┐ │   │
│  │ │ Filter Panel   │  Action Panel    │ │   │
│  │ │ (search, combo)│  (add,edit,del)  │ │   │
│  │ └────────────────┴──────────────────┘ │   │
│  ├──────────────────────────────────────┤   │
│  │ Content Panel (BorderLayout.CENTER)   │   │
│  │ (JTable / Custom content)             │   │
│  ├──────────────────────────────────────┤   │
│  │ Pagination Panel (BorderLayout.SOUTH) │   │
│  │ [Trang 1/5] [<] [>]                  │   │
│  └──────────────────────────────────────┘   │
└──────────────────────────────────────────────┘
```

### 5.2. Template Method Pattern

```java
public abstract class AbstractFeaturePanel extends JPanel {
    // Abstract methods — các panel con bắt buộc implement
    protected abstract JComponent createContentPanel(); // Nội dung chính
    protected abstract void loadData();                  // Load dữ liệu
    protected abstract String getItemLabel();            // Tên item (vd: "thí sinh")

    // Hook methods — có thể override
    protected Set<ToolbarAction> getToolbarActions() { /* default */ }
    protected JComponent createFilterPanel() { /* default search field */ }

    // Final method — không được override
    protected final void buildUI() {
        // 1. Tạo top section (filter + action)
        // 2. Tạo content panel
        // 3. Tạo pagination
        // 4. Gọi loadData()
    }
}
```

### 5.3. ToolbarAction Enum

**File:** [ToolbarAction.java](../../src/main/java/com/example/managementadmissionwf/ui/util/ToolbarAction.java)

```java
public enum ToolbarAction {
    ADD("Thêm", "add", GREEN, 100),
    EDIT("Sửa", "edit", BLUE, 100),
    DELETE("Xóa", "delete", RED, 100),
    REFRESH("Làm mới", "refresh", GRAY, 120),
    EXPORT_EXCEL("Xuất Excel", "export", ORANGE, 140),
    IMPORT_EXCEL("Nhập Excel", "import", PURPLE, 140),
}
```

Mỗi action định nghĩa: text, icon name, màu nền, chiều rộng.

---

## 6. UIFactory — Hệ thống tạo UI component

**File:** [UIFactory.java](../../src/main/java/com/example/managementadmissionwf/ui/util/UIFactory.java)

Factory class tạo các UI component với style đồng nhất:

### 6.1. Các factory method chính

| Method | Mô tả |
|--------|-------|
| `createTopSection()` | Panel chứa filter + action |
| `createFilterPanel()` | Panel chứa các field lọc |
| `createActionPanel(Set<ToolbarAction>)` | Panel chứa các nút hành động |
| `createPaginationPanel()` | Panel phân trang |
| `createActionButton(text, icon, color, width)` | JButton theo chuẩn |
| `createFilterTextField(columns, tooltip)` | JTextField cho filter |
| `createFilterCombo(items, width)` | JComboBox cho filter |
| `createStandardTable(model)` | JTable với style chuẩn |
| `createStandardScrollPane(table)` | JScrollPane cho table |

### 6.2. UIConstants — Định nghĩa style

**File:** [UIConstants.java](../../src/main/java/com/example/managementadmissionwf/ui/util/UIConstants.java)

```java
public class UIConstants {
    // Colors
    PRIMARY_BLUE = new Color(66, 133, 244);
    SUCCESS_GREEN = new Color(46, 204, 113);
    DANGER_RED = new Color(231, 76, 60);
    BACKGROUND_GRAY = new Color(240, 240, 240);

    // Fonts
    DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    BOLD_FONT = new Font("Segoe UI", Font.BOLD, 16);
    TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    // Dimensions
    FRAME_WIDTH = 1200;
    FRAME_HEIGHT = 800;
    SIDEBAR_WIDTH = 250;
}
```

---

## 7. Luồng xây dựng một Feature Panel cụ thể

### Ví dụ: CandidatePanel

**File:** [CandidatePanel.java](../../src/main/java/com/example/managementadmissionwf/ui/panel/candidate/CandidatePanel.java)

```
CandidatePanel extends AbstractFeaturePanel
       │
       ├─ getToolbarActions() → {ADD, EDIT, DELETE, REFRESH, EXPORT_EXCEL, IMPORT_EXCEL}
       │
       ├─ createFilterPanel() → Search + Khu vực + Đối tượng
       │
       ├─ createContentPanel() → CandidateListPanel (JTable)
       │
       ├─ loadData() → controller.searchCandidates(keyword, khuVuc, doiTuong)
       │
       └─ onToolbarAction(action) →
            ADD → controller.addCandidate()
            EDIT → controller.editCandidate()
            DELETE → controller.deleteCandidate()
            REFRESH → refreshData()
```

### Ví dụ: MajorPanel (Master-Detail)

**File:** [MajorPanel.java](../../src/main/java/com/example/managementadmissionwf/ui/panel/major/MajorPanel.java)

```
MajorPanel extends AbstractFeaturePanel
       │
       ├─ createContentPanel() →
       │     ┌──────────────────┬──────────────────┐
       │     │ MajorListPanel   │ Tổ hợp món panel  │
       │     │ (Master table)   │ (Detail table)    │
       │     └──────────────────┴──────────────────┘
       │
       └─ Chọn dòng trong master table →
            updateDetailTable(maNganh) → hiển thị tổ hợp môn tương ứng
```

---

## 8. Custom UI Components

### 8.1. Component Library

| Component | File | Mô tả |
|-----------|------|-------|
| `CustomButton` | [CustomButton.java](../../src/main/java/com/example/managementadmissionwf/ui/component/CustomButton.java) | JButton với hover effect |
| `CustomTextField` | [CustomTextField.java](../../src/main/java/com/example/managementadmissionwf/ui/component/CustomTextField.java) | JTextField với placeholder text |
| `CustomPasswordField` | [CustomPasswordField.java](../../src/main/java/com/example/managementadmissionwf/ui/component/CustomPasswordField.java) | JPasswordField với placeholder |

### 8.2. Form Dialogs

Mỗi feature có `FormDialog` riêng (vd: `CandidateFormDialog`, `MajorFormDialog`):

```
┌────────────────────────────────┐
│        FormDialog (JDialog)     │
│  ┌──────────────────────────┐ │
│  │ Form Fields              │ │
│  │ - TextField              │ │
│  │ - ComboBox               │ │
│  │ - TextArea               │ │
│  ├──────────────────────────┤ │
│  │ [Lưu]        [Hủy bỏ]   │ │
│  └──────────────────────────┘ │
└────────────────────────────────┘
```

**Pattern chung:**
- Constructor nhận data (null = thêm mới, có data = sửa)
- `validateForm()` kiểm tra input
- `isSaved()` trả về trạng thái lưu
- `getXxx()` trả về data từ form

---

## 9. Tóm tắt Design Patterns sử dụng

| Pattern | Ở đâu | Mục đích |
|---------|-------|----------|
| **Template Method** | `AbstractFeaturePanel` | Layout chuẩn cho tất cả panels |
| **Factory Method** | `UIFactory` | Tạo UI component đồng nhất |
| **MVC** | Panel + Controller + Service | Tách biệt view, logic, data |
| **Observer** | Table selection listeners | Master-detail view |
| **Command** | `ToolbarAction` + `onToolbarAction()` | Map nút → hành động |
| **Singleton** | `ApplicationContextHolder` | Truy cập Spring context từ mọi nơi |
| **Strategy** | Service interfaces/impls | Thay thế mock/real implementation |
