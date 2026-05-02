# Tài liệu: Luồng kết nối Backend - UI & Data Loading

## 1. Tổng quan kiến trúc kết nối

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐  │
│  │ FeaturePanel│  │ FormDialog   │  │ ListPanel         │  │
│  │ (View)      │  │ (Input)      │  │ (JTable display)  │  │
│  └──────┬──────┘  └──────┬───────┘  └────────▲──────────┘  │
│         │                │                    │              │
│         ▼                │                    │              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Controller (MVC Controller)            │    │
│  │   - Nhận input từ UI                               │    │
│  │   - Gọi service method                              │    │
│  │   - Xử lý response, update UI                       │    │
│  └──────────────────────┬──────────────────────────────┘    │
└─────────────────────────┼───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    Business Layer (BUS)                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Service Interface (bus/interfaces/)                 │   │
│  └──────────────────────┬───────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────▼───────────────────────────────┐   │
│  │  Service Implementation (bus/impl/)                  │   │
│  │  - Business logic                                    │   │
│  │  - Mock data (hiện tại) / Database (tương lai)      │   │
│  └──────────────────────┬───────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   Data Access Layer (DAL)                    │
│  ┌──────────────────┐  ┌────────────────────────────────┐   │
│  │ Repository       │  │ Entity (JPA)                   │   │
│  │ (Spring Data)    │  │                                │   │
│  └──────────────────┘  └────────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────▼───────────────────────────────┐   │
│  │  MySQL Database (application.properties)             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Dependency Injection — Cách UI lấy được Service

### 2.1. Cơ chế Spring DI cho Swing

Java Swing component không tự nhiên là Spring bean. Ứng dụng giải quyết bằng 3 cách:

#### Cách 1: `@Autowired` trên Controller (Spring bean)
```java
@Component
public class CandidateController {
    @Autowired
    private CandidateService candidateService;  // Spring tự inject
}
```

#### Cách 2: `ApplicationContextHolder` cho non-Spring objects
```java
// Trong RightLoginPanel (không phải Spring bean):
AuthService authService = ApplicationContextHolder.getBean(AuthService.class);
MainFrame mainFrame = ApplicationContextHolder.getBean(MainFrame.class);
```

#### Cách 3: `UIBeanConfig` — Đăng ký UI như Spring bean
```java
@Configuration
public class UIBeanConfig {
    @Bean @Scope("prototype")
    public MainFrame mainFrame() { return new MainFrame(); }
}
```

### 2.2. Mối quan hệ Dependency

```
ManagementAdmissionWfApplication
       │
       ├── ApplicationContextHolder (@Component)
       │        └── getBean() → Truy cập mọi Spring bean
       │
       ├── UIBeanConfig (@Configuration)
       │        └── Đăng ký: MainFrame, CandidatePanel, ScorePanel, ...
       │
       ├── LoginFrame (new) → ApplicationContextHolder.getBean(AuthService)
       │
       ├── MainFrame (Spring bean, prototype)
       │        ├── @Autowired CandidatePanel
       │        ├── @Autowired ScorePanel
       │        ├── @Autowired MajorPanel
       │        └── Navigation (inject tất cả panels)
       │
       └── Controllers (@Component)
                ├── CandidateController (@Autowired CandidateService)
                ├── ScoreController (@Autowired ScoreService)
                ├── MajorController (@Autowired MajorService)
                ├── UserController (@Autowired UserService)
                └── SubjectGroupController (@Autowired SubjectGroupService)
```

---

## 3. Luồng Data Loading chi tiết

### 3.1. Luồng chung — Load data khi mở Panel

```
MainFrame.setContent(panel)
       │
       ▼
panel.onShow() hoặc panel.buildUI()
       │
       ▼
AbstractFeaturePanel.loadData()   ← abstract method
       │
       ▼
[CandidatePanel.loadData()]       ← override bởi panel cụ thể
       │
       │  Lấy giá trị filter:
       │  keyword = txtSearch.getText()
       │  khuVuc = cboKhuVuc.getSelectedItem()
       │
       ▼
controller.searchCandidates(keyword, khuVuc, doiTuong)
       │
       ▼
candidateService.searchCandidates(keyword, khuVuc, doiTuong)
       │
       ├── [Mock] Lọc từ in-memory ArrayList
       └── [DB]   Gọi repository.query(...)
       │
       ▼
return List<CandidateDTO>
       │
       ▼
candidateListPanel.loadData(candidates)
       │
       ▼
DefaultTableModel.setRowCount(0)      // Xóa data cũ
for (CandidateDTO dto : candidates) {
    model.addRow(new Object[]{        // Thêm từng dòng
        dto.getCccd(), dto.getHo(), dto.getTen(), ...
    });
}
```

### 3.2. Luồng CRUD Operations

#### CREATE (Thêm mới)
```
User click [Thêm] button
       │
       ▼
AbstractFeaturePanel.onToolbarAction(ADD)
       │
       ▼
CandidatePanel.onToolbarAction(ADD)
       │
       ▼
controller.addCandidate()
       │
       ▼
new CandidateFormDialog(frame, null)  // null = thêm mới
dialog.setVisible(true)
       │
       ├── [Cancel] → return
       │
       └── [Save]
              │
              ▼
         dialog.validateForm()
              │
              ├── [Invalid] → JOptionPane.showMessageDialog(error)
              │
              └── [Valid]
                     │
                     ▼
                dialog.getCandidate() → CandidateDTO
                     │
                     ▼
                candidateService.createCandidate(dto)
                     │
                     ├── [Mock] candidates.add(dto)
                     └── [DB]   repository.save(entity)
                     │
                     ▼
                controller.loadAllCandidates()  // Refresh list
```

#### UPDATE (Sửa)
```
User chọn row trong table → Click [Sửa]
       │
       ▼
onToolbarAction(EDIT)
       │
       ▼
controller.editCandidate()
       │
       ▼
listPanel.getSelectedCandidate()  // Lấy row đang chọn
       │
       ▼
new CandidateFormDialog(frame, selectedCandidate)  // Có data = edit mode
dialog.setVisible(true)
       │
       └── [Save]
              │
              ▼
         dialog.validateForm() → dialog.getCandidate()
              │
              ▼
         candidateService.updateCandidate(dto)
              │
              ▼
         controller.loadAllCandidates()  // Refresh
```

#### DELETE (Xóa)
```
User chọn row → Click [Xóa]
       │
       ▼
onToolbarAction(DELETE)
       │
       ▼
controller.deleteCandidate()
       │
       ▼
listPanel.getSelectedCandidate()
       │
       ▼
JOptionPane.showConfirmDialog("Bạn có chắc chắn muốn xóa?")
       │
       ├── [No] → return
       │
       └── [Yes]
              │
              ▼
         candidateService.deleteCandidate(cccd)
              │
              ├── [Mock] candidates.removeIf(c -> c.getCccd().equals(cccd))
              └── [DB]   repository.softDelete() / repository.deleteById()
              │
              ▼
         controller.loadAllCandidates()
```

### 3.3. Luồng Search/Filter

```
User nhập vào search field → Nhấn Enter hoặc click Tìm kiếm
       │
       ▼
txtSearch.addActionListener(e -> loadData())
       │
       ▼
CandidatePanel.loadData()
       │
       ├─ Lấy keyword: txtSearch.getText().trim()
       ├─ Lấy filter: cboKhuVuc.getSelectedItem()
       ├─ Lấy filter: cboDoiTuong.getSelectedItem()
       │
       ▼
controller.searchCandidates(keyword, khuVuc, doiTuong)
       │
       ▼
candidateService.searchCandidates(keyword, khuVuc, doiTuong)
       │
       ├── [Mock] Stream filter trên List<CandidateDTO>
       │   candidates.stream()
       │       .filter(c -> keyword.isEmpty() || c matches keyword)
       │       .filter(c -> khuVuc.equals("Tất cả") || c.getKhuVuc().equals(khuVuc))
       │       .collect(Collectors.toList())
       │
       └── [DB]   repository.findByCriteria(keyword, khuVuc, doiTuong)
       │
       ▼
listPanel.loadData(filteredResults)
```

---

## 4. Chi tiết từng Controller - Service Mapping

### 4.1. Candidate (Thí sinh)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadAllCandidates()` | `candidateService.getAllCandidates()` | **Mock** ArrayList | Load toàn bộ thí sinh |
| `searchCandidates(kw, kv, dt)` | `candidateService.searchCandidates()` | **Mock** filter | Tìm kiếm + lọc |
| `addCandidate()` | `candidateService.createCandidate()` | **Mock** add | Thêm thí sinh mới |
| `editCandidate()` | `candidateService.updateCandidate()` | **Mock** replace | Cập nhật thí sinh |
| `deleteCandidate()` | `candidateService.deleteCandidate()` | **Mock** remove | Xóa thí sinh |

**Entity:** `XtThisinhxettuyen25` (CCCD là primary key)
**DTO:** `CandidateDTO`

### 4.2. Score (Điểm thi)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadAllScores()` | `scoreService.getAllScores()` | **Mock** ArrayList | Load toàn bộ điểm |
| `searchScores(cccd)` | `scoreService.getScoreByCccd()` | **Mock** filter | Tìm theo CCCD |
| `addScore()` | `scoreService.createScore()` | **Mock** add | Thêm điểm |
| `editScore()` | `scoreService.updateScore()` | **Mock** replace | Sửa điểm |
| `deleteScore()` | `scoreService.deleteScore()` | **Mock** remove | Xóa điểm |

**Entity:** `XtDiemthixettuyen` (liên kết với thí sinh qua CCCD)
**DTO:** `ScoreDTO`
**Business Logic:** `calculateTotalScore()` — tính tổng điểm các môn

### 4.3. Major (Ngành)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadAllMajors()` | `majorService.getAllMajors()` | **Mock** ConcurrentHashMap | Load toàn bộ ngành |
| `searchMajors(keyword)` | `majorService.searchMajors()` | **Mock** filter | Tìm kiếm ngành |
| `addMajor()` | `majorService.createMajor()` | **Mock** put | Thêm ngành mới |
| `editMajor()` | `majorService.updateMajor()` | **Mock** replace | Sửa ngành |
| `deleteMajor()` | `majorService.deleteMajor()` | **Mock** remove | Xóa ngành |
| `addSubjectGroup()` | `majorService.addSubjectGroup()` | **Mock** update | Gắn tổ hợp môn |
| `removeSubjectGroup()` | `majorService.removeSubjectGroup()` | **Mock** update | Bỏ tổ hợp môn |

**Entity:** `XtNganh` (mã ngành là ID)
**Đặc biệt:** Master-detail view — chọn ngành → hiển thị tổ hợp môn tương ứng qua `XtNganhTohop`

### 4.4. SubjectGroup (Tổ hợp môn)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadAllSubjectGroups()` | `subjectGroupService.getAll()` | **DB** Repository | Load toàn bộ |
| `search(keyword)` | `subjectGroupService.search()` | **DB** Repository | Tìm kiếm |
| `add()` | `subjectGroupService.create()` | **DB** Repository | Thêm mới |
| `edit()` | `subjectGroupService.update()` | **DB** Repository | Cập nhật |
| `delete()` | `subjectGroupService.delete()` | **DB** Repository | Xóa |

**Entity:** `XtTohopMonthi` (A00, D01, B00...)
**DTO:** `SubjectGroupResponse` / `SubjectGroupRequest`

### 4.5. User (Người dùng)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadUsers(page, role)` | `userService.getUsers(request)` | **DB** UserRepository | Phân trang + filter |
| `addUser()` | `userService.createUser(request)` | **DB** UserRepository | Tạo user mới |
| `editUser()` | `userService.updateUser(request)` | **DB** UserRepository | Cập nhật user |
| `deleteUser()` | `userService.deleteUser(id)` | **DB** UserRepository | Soft delete |

**Entity:** `Users` (ID tự tăng)
**DTO:** `CreateUserRequest`, `GetUserResponse`, `UpdateUserRequest`
**Đặc biệt:**
- Sử dụng `ApiResponse<Paging<GetUserResponse>>` wrapper (có pagination)
- Không cho phép tạo/cập nhật user với role ADMIN
- Soft delete pattern (`is_deleted` flag)
- Password mã hóa bằng BCrypt

**Luồng data với Pagination:**
```
controller.loadUsers(page, keyword, role)
       │
       ▼
userService.getUsers(GetUserRequest{page, keyword, role})
       │
       ▼
return ApiResponse<Paging<GetUserResponse>>
       │
       ▼
paging.getData()      → List<GetUserResponse>
paging.getTotalPages() → int
       │
       ▼
listPanel.loadData(paging.getData())            // Đổ data vào table
panel.updatePagination(paging)                  // Cập nhật pagination bar
```

### 4.6. Admission Result (Kết quả xét tuyển)

| Controller Method | Service Method | Data Source | Mô tả |
|-------------------|---------------|-------------|-------|
| `loadResults()` | `admissionResultService.getAllResults()` | **Mock** ArrayList | Load toàn bộ kết quả |
| `search(keyword)` | `admissionResultService.search()` | **Mock** filter | Tìm kiếm |
| `getByMajor(major)` | `admissionResultService.getByMajor()` | **Mock** filter | Lọc theo ngành |
| `updateResult()` | `admissionResultService.updateResult()` | **Mock** update | Cập nhật kết quả |
| `exportExcel()` | `admissionResultService.exportToExcel()` | **Mock** | Xuất Excel |
| `exportPDF()` | `admissionResultService.exportToPDF()` | **Mock** | Xuất PDF |

**Entity:** `XtNguyenvongxettuyen` (nguyện vọng xét tuyển)

---

## 5. Service Layer — Chi tiết Implementation

### 5.1. Mock Data Services (Phát triển)

Các service sau sử dụng **in-memory data** — dữ liệu mất khi restart app:

| Service | Data Structure | Sample Data |
|---------|---------------|-------------|
| `CandidateServiceImpl` | `ArrayList<CandidateDTO>` | 5 thí sinh mẫu |
| `ScoreServiceImpl` | `ArrayList<ScoreDTO>` | 5 bản điểm mẫu |
| `MajorServiceImpl` | `ConcurrentHashMap<Integer, MajorDTO>` | Auto-increment ID |
| `AdmissionResultServiceImpl` | `ArrayList<AdmissionResultDTO>` | 3 kết quả mẫu |

### 5.2. Real Database Services (Sản xuất)

Các service sau kết nối thật đến MySQL qua Spring Data JPA:

| Service | Repository | Mô tả |
|---------|-----------|-------|
| `AuthServiceImpl` | `UserRepository` | Xác thực, BCrypt password |
| `UserServiceImpl` | `UserRepository` | CRUD user, phân trang, soft delete |
| `SubjectGroupServiceImpl` | `SubjectGroupRepository` | CRUD tổ hợp môn |

### 5.3. Auth Service — Luồng đặc biệt

```
RightLoginPanel.handleLogin()
       │
       ▼
authService.login(username, password)
       │
       ▼
userRepo.findByUsername(username)
       │
       ├── [Not found] → throw AuthenticationException("Sai tên đăng nhập")
       │
       └── [Found]
              │
              ▼
         passwordEncoder.matches(password, user.getPassword())
              │
              ├── [Mismatch] → throw AuthenticationException("Sai mật khẩu")
              │
              └── [Match]
                     │
                     ▼
                authMapper.toLoginResponse(user)  // MapStruct: User → LoginResponseDTO
                     │
                     ▼
                authService.setCurrentUser(userDTO)
                     │
                     ▼
                return userDTO
```

---

## 6. Entity - DTO Mapping

### 6.1. Mapper Pattern (MapStruct)

```java
@Mapper(componentModel = "spring")
public interface AuthMapper {
    LoginResponseDTO toLoginResponse(Users user);
}

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(Users user);
    Users toEntity(UserDTO dto);
}

@Mapper(componentModel = "spring")
public interface SubjectGroupMapper {
    SubjectGroupResponse toResponse(XtTohopMonthi entity);
    XtTohopMonthi toEntity(SubjectGroupRequest request);
}
```

### 6.2. Entity Relationships

```
Users (1) ──────────────── (1) XtThisinhxettuyen25
                                  │
                                  │ (1)
                                  ▼
                            XtDiemthixettuyen (điểm thi)
                            XtDiemcongxettuyen (điểm cộng)

XtNganh (N) ──XtNganhTohop── (N) XtTohopMonthi
   (ngành)                   (tổ hợp môn)

XtNguyenvongxettuyen (nguyện vọng)
   ├── tham chiếu thí sinh (CCCD)
   ├── tham chiếu ngành (mã ngành)
   └── tham chiếu tổ hợp môn

XtBangquydoi (bảng quy đổi điểm chứng chỉ)
```

---

## 7. API Response Pattern

### 7.1. ApiResponse Wrapper (UserService)

```java
public class ApiResponse<T> {
    private int code;       // 200, 400, 500...
    private String message; // "Success", "Error"...
    private T data;         // Payload (List, Paging, Object)
}
```

### 7.2. Paging Wrapper

```java
public class Paging<T> {
    private List<T> data;        // Dữ liệu trang hiện tại
    private int currentPage;     // Trang hiện tại
    private int totalPages;      // Tổng số trang
    private long totalElements;  // Tổng số bản ghi
}
```

### 7.3. Import Result

```java
public class ImportResult<T> {
    private int successCount;
    private int failCount;
    private List<T> failedRecords;
    private List<String> errors;
}
```

---

## 8. Error Handling Flow

```
Controller method
       │
       ├── try {
       │       service.operation();
       │       panel.loadData(result);
       │       JOptionPane.showMessageDialog("Thành công!");
       │   }
       │
       └── catch (BusinessException e) {
              JOptionPane.showMessageDialog(e.getMessage(), "Lỗi", ERROR);
          }
       └── catch (AuthenticationException e) {
              JOptionPane.showMessageDialog(e.getMessage(), "Lỗi xác thực", ERROR);
          }
       └── catch (Exception e) {
              log.error("Unexpected error", e);
              JOptionPane.showMessageDialog("Đã xảy ra lỗi hệ thống");
          }
```

**Global Exception Handler:** `GlobalException` — bắt tất cả uncaught exceptions trên EDT.

---

## 9. Trạng thái hiện tại & Lộ trình chuyển đổi Mock → Database

### 9.1. Trạng thái hiện tại

| Module | Data Source | Trạng thái |
|--------|------------|------------|
| Authentication | **Database** | Hoàn thành |
| User Management | **Database** | Hoàn thành (có pagination) |
| Subject Group | **Database** | Hoàn thành |
| Candidate | **Mock** | Chưa kết nối DB |
| Score | **Mock** | Chưa kết nối DB |
| Major | **Mock** | Chưa kết nối DB |
| Admission Result | **Mock** | Chưa kết nối DB |
| Excel Import/Export | **Placeholder** | Chưa implement |

### 9.2. Lộ trình chuyển đổi Mock → DB

Để chuyển một service từ mock sang database, cần:

1. **Tạo Repository** (nếu chưa có):
   ```java
   public interface CandidateRepository extends JpaRepository<XtThisinhxettuyen25, String> {
       // Custom query methods
   }
   ```

2. **Sửa Service Implementation**:
   ```java
   @Service
   public class CandidateServiceImpl implements CandidateService {
       @Autowired
       private CandidateRepository repository;
       @Autowired
       private CandidateMapper mapper;

       @Override
       public List<CandidateDTO> getAllCandidates() {
           return repository.findAll().stream()
               .map(mapper::toDTO)
               .collect(Collectors.toList());
       }
   }
   ```

3. **Tạo Mapper** (nếu chưa có):
   ```java
   @Mapper(componentModel = "spring")
   public interface CandidateMapper {
       CandidateDTO toDTO(XtThisinhxettuyen25 entity);
       XtThisinhxettuyen25 toEntity(CandidateDTO dto);
   }
   ```

4. **Controller không cần sửa** — vì controller chỉ giao tiếp qua Service interface, không phụ thuộc vào implementation detail.
