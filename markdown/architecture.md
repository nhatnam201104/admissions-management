# Architecture Documentation

## Table of Contents
- [System Overview](#system-overview)
- [Architectural Principles](#architectural-principles)
- [Layered Architecture](#layered-architecture)
- [Component Architecture](#component-architecture)
- [Data Flow](#data-flow)
- [Design Patterns](#design-patterns)
- [Database Design](#database-design)
- [Security Architecture](#security-architecture)
- [UI Architecture](#ui-architecture)
- [Configuration Management](#configuration-management)
- [Error Handling](#error-handling)
- [Performance Considerations](#performance-considerations)
- [Scalability Considerations](#scalability-considerations)

---

## System Overview

The Admissions Management System is a desktop application built on a **hybrid architecture** that combines:

- **Spring Boot** backend providing business logic and data access
- **Java Swing** frontend for rich desktop user experience
- **MySQL** database for persistent storage
- **Layered architecture** for separation of concerns

### System Goals

1. **Modularity**: Clear separation between UI, business logic, and data access
2. **Maintainability**: Easy to understand, modify, and extend
3. **Testability**: Components can be tested independently
4. **Extensibility**: New features can be added without major refactoring
5. **Security**: Role-based access control and secure authentication

---

## Architectural Principles

### 1. Separation of Concerns
Each layer has a specific responsibility:
- **UI Layer**: Presentation and user interaction
- **Business Layer**: Business logic and rules
- **Data Access Layer**: Database operations

### 2. Dependency Inversion
High-level modules (services) don't depend on low-level modules (repositories). Both depend on abstractions (interfaces).

### 3. Single Responsibility
Each class has one reason to change, following SOLID principles.

### 4. Interface Segregation
Clients depend only on interfaces they use, not on interfaces they don't.

### 5. Don't Repeat Yourself (DRY)
Common functionality is abstracted into reusable components.

---

## Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Java Swing UI Components                        │   │
│  │  - LoginFrame, MainFrame                        │   │
│  │  - Panels (Candidate, Score, Major, etc.)        │   │
│  │  - Custom Components (Button, TextField)        │   │
│  │  - Navigation Controller                        │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   Business Logic Layer                   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Service Interfaces (bus/interfaces/)            │   │
│  │  - AuthService, StudentService, etc.             │   │
│  │                                                  │   │
│  │  Service Implementations (bus/impl/)              │   │
│  │  - AuthServiceImpl, StudentServiceImpl, etc.     │   │
│  └──────────────────────────────────────────────────┘   │
│                       │                                  │
│              DTOs & Mappers                            │
│              - Entity ↔ DTO conversion                  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   Data Access Layer                      │
│  ┌──────────────────────────────────────────────────┐   │
│  │  JPA Repositories (dal/repository/)               │   │
│  │  - UserRepository, StudentRepository, etc.       │   │
│  │                                                  │   │
│  │  JPA Entities (dal/entity/)                      │   │
│  │  - Users, Students, Major, Subject, etc.         │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   Database Layer                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │  MySQL Database                                 │   │
│  │  - Tables mapped to JPA Entities                │   │
│  │  - Foreign key relationships                    │   │
│  │  - Indexes and constraints                       │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### Presentation Layer (UI)
- **Responsibility**: User interface and interaction
- **Components**:
  - Frames: LoginFrame, MainFrame
  - Panels: CandidatePanel, ScorePanel, WishPanel, MajorPanel, etc.
  - Components: CustomButton, CustomTextField, CustomPasswordField
  - Navigation: Navigation controller
- **Patterns Used**:
  - MVC (Model-View-Controller)
  - Observer Pattern (for event handling)
  - Lazy Loading (panels created on demand)

#### Business Logic Layer
- **Responsibility**: Business rules and application logic
- **Components**:
  - Service Interfaces: Define contract
  - Service Implementations: Implement business logic
  - DTOs: Data transfer objects
  - Mappers: Entity-DTO conversion using MapStruct
- **Patterns Used**:
  - Service Layer Pattern
  - DTO Pattern
  - Strategy Pattern (multiple authentication strategies)

#### Data Access Layer
- **Responsibility**: Database operations
- **Components**:
  - JPA Entities: Database table mappings
  - JPA Repositories: CRUD operations
  - Hibernate ORM: Object-relational mapping
- **Patterns Used**:
  - Repository Pattern
  - Active Record Pattern (via JPA)
  - Unit of Work Pattern (via EntityManager)

---

## Component Architecture

### Core Components

#### 1. Application Bootstrap
```java
ManagementAdmissionWfApplication.java
```
- Entry point for Spring Boot application
- Initializes Spring context
- Launches Swing UI on EDT (Event Dispatch Thread)
- Sets up global exception handler

#### 2. Authentication System
```
AuthService (interface)
    └── AuthServiceImpl
        ├── UserRepository
        ├── UserMapper
        └── PasswordEncoder
```

**Flow**:
1. User enters credentials in LoginFrame
2. LoginPanel validates input
3. AuthServiceImpl authenticates user
4. Returns LoginResponseDTO with user info
5. MainFrame launched with user role

#### 3. Student Management
```
StudentService (interface)
    └── StudentServiceImpl (TODO)
        ├── StudentRepository
        ├── StudentMapper
        └── Validator
```

#### 4. UI Navigation
```
Navigation (controller)
    ├── MainFrame
    └── Multiple Panels
        ├── CandidatePanel
        ├── ScorePanel
        ├── WishPanel
        ├── MajorPanel
        ├── ThresholdPanel
        ├── SubjectGroupPanel
        ├── AdmissionPanel
        └── StatisticPanel
```

**Pattern**: Lazy loading - panels created only when needed

---

## Data Flow

### Authentication Flow

```
┌──────────────┐
│   User       │
│  (Login)     │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ LoginFrame   │
│  (UI)        │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ RightLogin   │
│  Panel       │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ AuthService  │
│  .login()    │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│UserRepository│
│ .findBy     │
│  username()  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Database   │
│  (Users)     │
└──────────────┘
```

### Panel Navigation Flow

```
User clicks menu button
       │
       ▼
Navigation.actionPerformed()
       │
       ▼
Check if panel exists (lazy load)
       │
       ▼
MainFrame.setContent(panel)
       │
       ▼
MainFrame.highlightMenuButton()
       │
       ▼
Panel displayed with animation
```

### Data Operation Flow (Example: Create Student)

```
┌──────────────┐
│   User       │
│  (Input)     │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Panel UI    │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ StudentDTO   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│StudentMapper │
│ .toEntity()  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│StudentService│
│ .create()    │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│StudentRepo   │
│ .save()      │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Database   │
└──────────────┘
```

---

## Design Patterns

### 1. Model-View-Controller (MVC)

**Implementation**:
- **Model**: JPA Entities, DTOs
- **View**: Swing Frames and Panels
- **Controller**: Navigation class, Action Listeners

**Benefits**:
- Separation of concerns
- Independent testing
- Easy maintenance

### 2. Repository Pattern

**Implementation**:
```java
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

**Benefits**:
- Abstraction over data access
- Easy to mock for testing
- Switchable data source

### 3. Data Transfer Object (DTO) Pattern

**Implementation**:
```java
// DTOs in dto/ package
// Mappers in mapper/ package
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(Users user);
    Users toUser(UserDTO userDTO);
}
```

**Benefits**:
- Decouple entities from external layers
- Control what data is exposed
- Optimize data transfer

### 4. Service Layer Pattern

**Implementation**:
```java
public interface AuthService {
    LoginResponseDTO login(LoginDTO loginDTO);
}

@Service
public class AuthServiceImpl implements AuthService {
    // Implementation
}
```

**Benefits**:
- Encapsulates business logic
- Transaction management
- Reusable across different UI components

### 5. Builder Pattern

**Implementation**:
```java
@Entity
@Data
@Builder
public class Students {
    // Lombok @Builder generates builder
}

// Usage
Students student = Students.builder()
    .address("123 Street")
    .school("High School")
    .build();
```

**Benefits**:
- Clear object construction
- Optional parameters
- Immutable objects (if needed)

### 6. Lazy Loading Pattern

**Implementation**:
```java
private void showCandidatePanel() {
    if (candidatePanel == null) {
        candidatePanel = new CandidatePanel();
    }
    mainFrame.setContent(candidatePanel);
}
```

**Benefits**:
- Improved startup time
- Reduced memory usage
- On-demand resource loading

### 7. Observer Pattern

**Implementation**:
```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle event
    }
});
```

**Benefits**:
- Loose coupling
- Event-driven architecture
- Multiple listeners support

---

## Database Design

The database follows Vietnamese University Admission System (xettuyen2026) design, organized into 3 logical layers:

### 1. MASTER DATA (DỮ LIỆU DANH MỤC)
**Entities**: xt_nganh, xt_tohop_monthi, xt_nganh_tohop

### 2. CANDIDATE DATA (DỮ LIỆU THÍ SINH)
**Entities**: xt_thisinhxettuyen25, xt_diemthixettuyen, xt_diemcongxettuyen

### 3. ADMISSION PROCESSING (XỬ LÝ XÉT TUYỂN)
**Entities**: xt_nguyenvongxettuyen, xt_bangquydoi

### Entity Relationship Diagram

```
┌─────────────────────────┐       ┌─────────────────────────┐
│   xt_thisinhxettuyen25 │       │   xt_diemthixettuyen │
│   ───────────────────  │       │   ───────────────────  │
│   PK id                │       │   PK id                │
│   PK cccd (unique)     │◄──────│   PK cccd (unique)     │
│   PK sobaodanh (unique) │       │   PK sobaodanh (unique) │
│   ho, ten              │       │   d_phuongthuc          │
│   ngay_sinh, email...    │       │   TO, LI, HO, SI...     │
└──────────┬────────────┘       │   N1_THI, N1_CC         │
           │                     │   NL1, NK1, NK2         │
           │                     └─────────────────────────┘
           ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│xt_nguyenvongxettuyen │       │  xt_diemcongxettuyen  │
│─────────────────────────│       │─────────────────────────│
│ PK id                │◄──────│ PK id                 │
│ PK nn_cccd           │       │ PK cccd (unique)     │
│ PK nv_manganh         │       │ diemCC, diemUtxt      │
│ PK nv_tt              │       │ diemTong              │
│ diem_thxt            │       └─────────────────────────┘
│ diem_utqd, diem_cong │
│ diem_xettuyen         │
│ nv_ketqua            │
└──────────┬────────────┘
           │
           │ nv_manganh
           ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│     xt_nganh          │       │  xt_nganh_tohop       │
│  ───────────────────  │       │  ───────────────────  │
│  PK idnganh          │       │  PK id                 │
│  PK manganh (unique)  │◄──────│  PK manganh            │
│  tennganh             │       │  PK matohop            │
│  n_chitieu            │       │  th_mon1, hsmon1        │
│  n_diemsan...         │       │  th_mon2, hsmon2        │
│  sl_xtt, sl_dgnl...    │       │  th_mon3, hsmon3        │
└──────────┬────────────┘       └──────────┬─────────────┘
           │                             │
           │                             │ matohop
           └──────────┬──────────────────┘
                      │
                      ▼
           ┌─────────────────────────┐
           │  xt_tohop_monthi    │
           │  ───────────────────  │
           │  PK id              │
           │  PK matohop (unique) │
           │  mon1, mon2, mon3  │
           │  tentohop            │
           └─────────────────────────┘

┌─────────────────────────┐
│   xt_bangquydoi      │
│  ───────────────────  │
│  PK id               │
│  d_phuongthuc         │
│  d_tohop              │
│  d_mon                │
│  d_diema, d_diemb     │
│  d_diemc, d_diemd     │
└─────────────────────────┘
```

### Data Flow - Admission Processing

```
┌─────────────────────────────────────────────────────────┐
│         Thí sinh nhập thông tin                 │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   xt_thisinhxettuyen25 (Thông tin cá nhân)       │
│   - CCCD, SBD, Họ, Tên, Email...            │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   xt_diemthixettuyen (Điểm thi)               │
│   - Điểm các môn (TO, LI, HO, SI, SU, DI, VA)   │
│   - Điểm ngoại ngữ (N1_THI, N1_CC)             │
│   - Điểm ĐGNL (NL1, NK1, NK2)                  │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   xt_diemcongxettuyen (Điểm cộng)                │
│   - Điểm chứng chỉ (IELTS, SAT)                   │
│   - Điểm ưu tiên đặc biệt (HSG, thể thao)            │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   Thí sinh đăng ký nguyện vọng                   │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   xt_nguyenvongxettuyen (Nguyện vọng)            │
│   - Chọn ngành và tổ hợp môn                     │
│   - Xếp thứ tự nguyện vọng (1, 2, 3...)          │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   Tính điểm xét tuyển                          │
│                                                  │
│   diem_thxt = mon1*hs1 + mon2*hs2 + mon3*hs3    │
│   diem_xettuyen = diem_thxt + diem_utqd + diem_cong │
└──────────────┬────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────┐
│   Ranking theo ngành                            │
│   - So sánh điểm với chỉ tiêu                    │
│   - Sắp xếp thí sinh theo điểm giảm dần            │
│   - Đánh dấu TRUNG_TUYEN/CHƯA_XÉT/TRƯỢT        │
└─────────────────────────────────────────────────────────┘
```

### Database Schema Details

#### Users Table
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'MANAGER', 'ADMIN') NOT NULL,
    created_at DATE,
    updated_at DATE,
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

#### Students Table
```sql
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    address VARCHAR(255),
    school VARCHAR(255),
    score INT DEFAULT 0,
    addition_score INT DEFAULT 0,
    total_aspiration INT DEFAULT 0,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### Subject Table
```sql
CREATE TABLE subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at DATE,
    updated_at DATE
);
```

#### ComplexSubject (Subject Combination) Table
```sql
CREATE TABLE complex_subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    subject_id_1 INT,
    subject_id_2 INT,
    subject_id_3 INT,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (subject_id_1) REFERENCES subject(id),
    FOREIGN KEY (subject_id_2) REFERENCES subject(id),
    FOREIGN KEY (subject_id_3) REFERENCES subject(id)
);
```

#### Major Table
```sql
CREATE TABLE major (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    hired INT DEFAULT 0,
    target_quantity INT DEFAULT 0,
    created_at DATE,
    updated_at DATE
);
```

#### MajorDetail Table
```sql
CREATE TABLE major_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    major_id INT,
    complex_subject_id INT,
    total_score INT NOT NULL,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (major_id) REFERENCES major(id),
    FOREIGN KEY (complex_subject_id) REFERENCES complex_subject(id)
);
```

#### StudentSubject Table
```sql
CREATE TABLE student_subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    subject_id INT,
    score INT NOT NULL,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (subject_id) REFERENCES subject(id)
);
```

#### Aspiration Table
```sql
CREATE TABLE aspiration (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `index` INT NOT NULL,
    student_id INT,
    major_detail_id INT,
    is_hire BOOLEAN DEFAULT FALSE,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (major_detail_id) REFERENCES major_detail(id),
    UNIQUE KEY uk_student_index (student_id, `index`)
);
```

### Relationships

1. **Users ↔ Students**: One-to-One
   - Each user has zero or one student profile
   - Used for role-based data access

2. **Students ↔ StudentSubject**: One-to-Many
   - Each student has multiple subject scores
   - Essential for calculating total scores

3. **Students ↔ Aspiration**: One-to-Many
   - Each student can have multiple aspiration preferences
   - Ordered by `index` field

4. **Subject ↔ ComplexSubject**: Many-to-Many
   - Each subject can be in multiple combinations
   - Each combination has multiple subjects
   - Implemented via foreign keys

5. **Major ↔ MajorDetail**: One-to-Many
   - Each major can have multiple subject combinations
   - Different combinations may have different thresholds

6. **ComplexSubject ↔ MajorDetail**: One-to-Many
   - Each subject combination can be used by multiple majors

### Indexes

- **Users**: `username`, `email` (unique)
- **Aspiration**: `(student_id, index)` (composite unique)
- **Foreign keys**: Automatically indexed by MySQL

---

## Security Architecture

### Authentication Flow

```
┌─────────────────────────────────────────┐
│         User Login Request             │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│     LoginFrame (Swing UI)               │
│  - Input validation                     │
│  - Password masking                      │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│     AuthServiceImpl                     │
│  - Retrieve user by username            │
│  - Verify password with BCrypt          │
│  - Return user profile with role        │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│     UserMapper                          │
│  - Convert Entity to DTO                │
│  - Filter sensitive data                │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│     LoginResponseDTO                    │
│  - User information                     │
│  - Authentication success token         │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│     MainFrame Launch                    │
│  - Set user session                     │
│  - Show appropriate menu items          │
└─────────────────────────────────────────┘
```

### Role-Based Access Control (RBAC)

#### Role Definitions

```java
public enum RoleUser {
    STUDENT,   // View personal data only
    MANAGER,   // Manage admissions data
    ADMIN      // Full system access
}
```

#### Access Control Implementation

**UI Level**:
```java
// In MainFrame.java
if (user.getRole().toString().equalsIgnoreCase("admin")) {
    menuPanel.add(menuAdmission);
    menuPanel.add(menuStatistic);
}
```

**Service Level** (TODO):
```java
@PreAuthorize("hasRole('ADMIN')")
public void processAdmissions() {
    // Only admin can process admissions
}
```

### Password Security

#### BCrypt Password Encoding

```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Features**:
- Automatic salt generation
- Adaptive hash cost factor
- Built-in protection against rainbow tables
- Industry-standard algorithm

### Security Best Practices

1. **Password Storage**: Never store plain text passwords
2. **SQL Injection Prevention**: Use parameterized queries (JPA)
3. **Input Validation**: Jakarta Validation on all DTOs
4. **Error Messages**: Generic messages to prevent enumeration
5. **Session Management**: User session maintained in application context

---

## UI Architecture

### Component Hierarchy

```
JFrame
├── LoginFrame
│   ├── rootPanel (BorderLayout)
│   │   ├── LeftWelcomePanel
│   │   └── RightLoginPanel
│   │       ├── CustomTextField (username)
│   │       ├── CustomPasswordField (password)
│   │       └── CustomButton (login)
│
└── MainFrame
    ├── rootPanel (BorderLayout)
    │   ├── sidebarPanel (BoxLayout.Y_AXIS)
    │   │   ├── userSection
    │   │   │   ├── userIconLabel
    │   │   │   └── usernameLabel
    │   │   └── menuPanel
    │   │       ├── menuCandidate
    │   │       ├── menuScore
    │   │       ├── menuWish
    │   │       ├── menuMajor
    │   │       ├── menuThreshold
    │   │       ├── menuSubjectGroup
    │   │       ├── menuAdmission (Admin only)
    │   │       └── menuStatistic (Admin only)
    │   │
    │   └── contentPanel (BorderLayout)
    │       └── [Dynamic Panel]
    │           ├── CandidatePanel
    │           ├── ScorePanel
    │           ├── WishPanel
    │           ├── MajorPanel
    │           ├── ThresholdPanel
    │           ├── SubjectGroupPanel
    │           ├── AdmissionPanel
    │           └── StatisticPanel
```

### UI Design Patterns

#### 1. Panel-Based Navigation
- Dynamic content switching
- State preservation per panel
- Lazy initialization

#### 2. Custom Component Library
```java
// CustomButton.java
// CustomTextField.java
// CustomPasswordField.java
```

**Benefits**:
- Consistent styling
- Reusable components
- Centralized UI constants

#### 3. Responsive Layout
- Uses BorderLayout for main structure
- BoxLayout for vertical stacking
- FlowLayout for horizontal arrangements

### UI Constants

```java
public class UIConstants {
    public static final int FRAME_WIDTH = 1200;
    public static final int FRAME_HEIGHT = 800;
    public static final Color BACKGROUND_GRAY = new Color(245, 245, 245);
    public static final Color SIDEBAR_COLOR = new Color(44, 62, 80);
    // ... more constants
}
```

### Event Handling

**Menu Click Events**:
```java
menuCandidate.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        showCandidatePanel();
    }
});
```

**Mouse Hover Effects**:
```java
button.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent evt) {
        button.setBackground(new Color(52, 152, 219));
    }
    public void mouseExited(MouseEvent evt) {
        button.setBackground(new Color(44, 62, 80));
    }
});
```

### Thread Safety

**SwingUtilities.invokeLater**:
```java
SwingUtilities.invokeLater(() -> {
    loginFrame.setVisible(true);
});
```

**Purpose**:
- Ensures UI updates on Event Dispatch Thread (EDT)
- Prevents race conditions
- Maintains Swing single-threaded rule

---

## Configuration Management

### Application Properties

```properties
# Application Configuration
spring.application.name=management-admission-wf

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/admission_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.defer-datasource-initialization=true
spring.jmx.enabled=false

# SQL Initialization
spring.sql.init.mode=always

# Logging Configuration
logging.level.com.example.managementadmissionwf=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Configuration Classes

#### AppConfig
```java
@Configuration
public class AppConfig {
    // Application-specific configuration beans
}
```

#### ApplicationContextHolder
```java
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;
    
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
```

**Purpose**:
- Access Spring beans from non-Spring classes
- Useful in Swing components that aren't Spring-managed

#### SecurityConfig
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Error Handling

### Exception Hierarchy

```
Exception
├── BusinessException (Custom)
│   ├── AuthenticationException
│   └── ResourceNotFoundException
└── GlobalException (Thread.UncaughtExceptionHandler)
```

### Custom Exceptions

#### AuthenticationException
```java
public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message);
    }
}
```

**Usage**:
```java
if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
    throw new AuthenticationException("Invalid username or password.");
}
```

#### ResourceNotFoundException
```java
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

#### GlobalException
```java
public class GlobalException implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Log exception
        // Show user-friendly error dialog
        // Optionally restart application
    }
}
```

### Exception Handling Strategy

1. **Validation Layer**: Jakarta Validation on DTOs
2. **Service Layer**: Business exceptions with clear messages
3. **UI Layer**: User-friendly error dialogs
4. **Global Handler**: Catch-all for unexpected errors

---

## Performance Considerations

### 1. Database Performance

**Connection Pooling** (Spring Boot Default):
- HikariCP connection pool
- Automatic configuration
- Optimal for most use cases

**Query Optimization**:
- Use `@Query` with JPQL for complex queries
- Lazy loading for relationships
- Proper indexing on foreign keys

**Hibernate DDL Auto**:
```properties
spring.jpa.hibernate.ddl-auto=update
```
- Development: `update` or `create-drop`
- Production: `validate` or `none`

### 2. UI Performance

**Lazy Loading**:
```java
private void showCandidatePanel() {
    if (candidatePanel == null) {
        candidatePanel = new CandidatePanel();  // Create only when needed
    }
    mainFrame.setContent(candidatePanel);
}
```

**Panel Caching**:
- Panels created once and reused
- Maintains state between navigation
- Reduces memory allocation

### 3. Memory Management

**Entity Management**:
```java
@Transactional
public StudentDTO getStudentById(Integer id) {
    // Transaction ensures proper entity lifecycle
    // Entities detached after transaction end
}
```

**DTO Pattern Benefits**:
- Reduces memory footprint
- Avoids loading unnecessary relationships
- Prevents serialization issues

### 4. Caching Strategy (TODO)

**Potential Implementations**:
- Spring Cache Abstraction
- Redis for distributed caching
- Ehcache for local caching

---

## Scalability Considerations

### Current Limitations

1. **Single-User Desktop App**: Not designed for concurrent users
2. **Local Database**: MySQL running on same machine
3. **Monolithic Architecture**: All functionality in one application

### Future Scalability Options

#### 1. Multi-Tier Architecture

```
┌─────────────────┐
│   Swing Client  │  (Multiple instances)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   REST API      │  (Spring Boot)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   MySQL Cluster │  (Distributed)
└─────────────────┘
```

#### 2. Microservices

Split into services:
- User Service
- Student Service
- Admission Service
- Reporting Service

#### 3. Database Scaling

**Horizontal Scaling**:
- MySQL Cluster
- Read replicas
- Connection pooling

**Vertical Scaling**:
- Increase database resources
- Optimize queries
- Add indexes

### Migration Path

**Phase 1**: Add REST API layer
- Keep Swing UI as client
- Expose business logic via HTTP

**Phase 2**: Separate frontend
- Convert Swing to Web (React/Vue)
- REST API remains same

**Phase 3**: Microservices
- Split functionality into services
- Use API Gateway

---

## Technology Deep Dive

### MapStruct Integration

**Configuration**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Benefits**:
- Compile-time code generation
- Type-safe mapping
- Better performance than reflection
- IDE support

### Lombok Integration

**Configuration**:
```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

**Common Annotations**:
- `@Data`: Getters, setters, toString, equals, hashCode
- `@Builder`: Builder pattern
- `@AllArgsConstructor`, `@NoArgsConstructor`: Constructors
- `@RequiredArgsConstructor`: Constructor for final fields

---

## Testing Strategy

### Unit Testing (TODO)

**Service Layer**:
```java
@SpringBootTest
class AuthServiceTest {
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    @Test
    void testLoginSuccess() {
        // Given
        // When
        // Then
    }
}
```

**Repository Layer**:
```java
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUsername() {
        // Test repository methods
    }
}
```

### Integration Testing (TODO)

**Full Flow**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {
    // Test complete user flows
}
```

### UI Testing

**Manual Testing**:
- Test all panels
- Verify navigation
- Check error handling

**Automated UI Testing** (Future):
- AssertJ Swing
- TestFX

---

## Deployment Architecture

### Current Deployment

**Local Desktop**:
```
┌─────────────────────────────┐
│   User's Machine            │
│  ┌───────────────────────┐  │
│  │  JRE 21               │  │
│  │  ┌─────────────────┐  │  │
│  │  │  Application    │  │  │
│  │  │  JAR            │  │  │
│  │  └─────────────────┘  │  │
│  │  ┌─────────────────┐  │  │
│  │  │  MySQL Server   │  │  │
│  │  │  (Local)        │  │  │
│  │  └─────────────────┘  │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### Network Deployment (Future)

```
┌──────────────┐         ┌──────────────┐
│  Client 1    │         │  Client 2    │
│  (Swing)     │         │  (Swing)     │
└──────┬───────┘         └──────┬───────┘
       │                        │
       └────────┬───────────────┘
                │
                ▼
┌─────────────────────────────┐
│   Application Server        │
│  ┌───────────────────────┐  │
│  │  Spring Boot App      │  │
│  │  (REST API)           │  │
│  └───────────────────────┘  │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│   Database Server           │
│  ┌───────────────────────┐  │
│  │  MySQL Cluster        │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

---

## Monitoring and Logging

### Logging Configuration

```properties
logging.level.com.example.managementadmissionwf=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**Logging Strategy**:
- Development: DEBUG level
- Production: INFO/WARN level
- Separate log files for different components

### Monitoring (TODO)

**Potential Implementations**:
- Spring Boot Actuator
- Custom metrics collection
- Performance monitoring

---

## Documentation Standards

### Code Documentation

**JavaDoc Comments**:
```java
/**
 * Authenticate user with username and password
 * 
 * @param loginDTO Login credentials
 * @return LoginResponseDTO containing authentication result and user profile
 * @throws AuthenticationException if credentials are invalid
 */
LoginResponseDTO login(LoginDTO loginDTO);
```

**Inline Comments**:
- Complex business logic
- Non-obvious implementation details
- Workarounds or temporary solutions

---

## Migration from Legacy Entities

The system is transitioning from legacy entities to new Xt* entities that follow Vietnamese Ministry of Education standards. This migration improves data organization and supports multi-method admission processing.

### Legacy Entities (Deprecated)

| Entity | Status | Replacement |
|--------|--------|-------------|
| `Students` | ⚠️ Deprecated | `XtThisinhxettuyen25` |
| `Major` | ⚠️ Deprecated | `XtNganh` |
| `MajorDetail` | ⚠️ Deprecated | `XtNganhTohop` |
| `ComplexSubject` | ⚠️ Deprecated | `XtTohopMonthi` |
| `Subject` | ⚠️ Deprecated | Embedded in `XtTohopMonthi` |
| `StudentSubject` | ⚠️ Deprecated | `XtDiemthixettuyen` |
| `Aspiration` | ⚠️ Deprecated | `XtNguyenvongxettuyen` |

### Key Changes

1. **CCCD as Primary Identity**: Replacing `user_id` with `cccd` for candidate identification
2. **Score Storage**: Moving from normalized `student_subject` table to denormalized `xt_diemthixettuyen` with subject-specific fields
3. **Bonus Points**: Separate table `xt_diemcongxettuyen` for certificate and special priority points
4. **Multi-Method Admission**: Support for THPT, DGNL, VSAT, and Direct Admission methods
5. **Flexible Subject Combinations**: Hệ số môn (subject coefficients) can vary by major

### Migration Benefits

- ✅ Better alignment with Vietnamese admission regulations
- ✅ Support for multiple admission methods
- ✅ More flexible score calculation
- ✅ Easier certificate conversion with `xt_bangquydoi`
- ✅ Better tracking of admission statistics by method

### Migration Steps

For detailed migration instructions, see [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md).

### Preserved Entities

- `Users` - Authentication and user management (kept unchanged)
- `RoleUser` - Role definitions (kept unchanged)

---

## Conclusion

The Admissions Management System architecture follows modern software engineering principles and patterns. The layered architecture ensures separation of concerns, while the use of Spring Boot and Java Swing provides a robust foundation for both business logic and user interface.

### Key Strengths

1. **Clear Architecture**: Well-defined layers and responsibilities
2. **Modern Stack**: Spring Boot, JPA, Java 21
3. **Security**: BCrypt encryption, RBAC
4. **Extensibility**: Easy to add new features
5. **Maintainability**: Clean code, proper documentation
6. **Vietnamese Education Standards**: New Xt* entities follow Ministry of Education requirements

### Areas for Improvement

1. **Complete Migration**: Migrate all services from legacy entities to Xt* entities
2. **Complete Service Implementations**: StudentService and others using Xt* entities
3. **Add Comprehensive Tests**: Unit and integration tests for Xt* entities
4. **Implement Caching**: For improved performance
5. **Add REST API**: For multi-user support
6. **Implement Export Features**: Excel, PDF reports

---

**Last Updated**: March 2026  
**Version**: 1.1.0  
**Author**: Development Team
