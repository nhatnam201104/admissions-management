# Admissions Management System (Admissions WF)

A comprehensive Java Swing desktop application for managing university admissions, built with Spring Boot backend and modern architectural patterns.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [User Roles](#user-roles)
- [Database Schema](#database-schema)
- [Development](#development)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Admissions Management System is a desktop application designed to streamline the university admission process. It provides a complete solution for managing candidates, scores, subject combinations, majors, admission thresholds, and admission results.

The application uses a modern technology stack combining Spring Boot for backend services with Java Swing for a rich desktop user interface.

## ✨ Features

### Core Functionality

- **User Authentication**: Secure login system with role-based access control
- **Candidate Management**: Add, edit, and manage candidate information
- **Score Management**: Track and manage candidate subject scores
- **Aspiration/Wish Management**: Handle candidate preference rankings for majors
- **Major Management**: Manage academic majors and their details
- **Admission Threshold Management**: Set and update admission score thresholds
- **Subject Combination Management**: Define and manage subject combinations (A00, A01, etc.)
- **Admission Results**: Process and view admission results
- **Statistics & Reports**: Generate comprehensive admission statistics

### User Roles

- **STUDENT**: View personal information, scores, and aspirations
- **MANAGER**: Manage candidates, scores, majors, and thresholds
- **ADMIN**: Full access including admission processing and statistics

## 🛠 Technology Stack

### Backend

- **Java 21**: Modern Java with preview features enabled
- **Spring Boot 4.0.2**: Application framework
- **Spring Data JPA**: Database operations with Hibernate ORM
- **Spring Security**: Password encryption and authentication
- **MySQL**: Relational database
- **MapStruct 1.6.0**: Entity-DTO mapping
- **Lombok**: Reduce boilerplate code
- **Bean Validation (Jakarta Validation)**: Input validation

### Frontend

- **Java Swing**: Desktop GUI framework
- **Swing Components**: Custom styled buttons, text fields, panels
- **MVC Pattern**: Separation of UI, business logic, and data

### Build Tools

- **Maven**: Dependency management and build automation
- **Maven Compiler Plugin**: Java 21 compilation with preview features

## 🏗 Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         UI Layer (Swing)            │
│  Frames, Panels, Components         │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│      Business Layer (Services)      │
│  AuthService, StudentService, etc.  │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│    Data Access Layer (Repositories)  │
│  JPA Repositories                   │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│    Database Layer (MySQL)           │
│  Entities with Hibernate ORM        │
└─────────────────────────────────────┘
```

### Key Design Patterns

- **MVC (Model-View-Controller)**: Separates concerns between data, UI, and logic
- **Repository Pattern**: Abstracts database operations
- **DTO Pattern**: Data transfer objects for layer communication
- **Builder Pattern**: Object construction with Lombok
- **Lazy Loading**: Panels are created on demand for performance

## 📦 Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK) 21** or higher
- **Apache Maven 3.6+**
- **MySQL Server 5.7+** or **MySQL 8.0+**
- **Git** (for cloning the repository)

## 🔧 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/nhatnam201104/admissions-management.git
cd admissions-management
```

### 2. Database Setup

Create a MySQL database and configure the connection:

```sql
-- The application will auto-create the database if it doesn't exist
-- Manual setup (optional):
CREATE DATABASE admission_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/admission_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build the Project

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Or using Maven directly
mvn clean install
```

## 🚀 Running the Application

### Start the Application

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run

# Or run the JAR file after building
java -jar target/management-admission-wf-0.0.1-SNAPSHOT.jar
```

### Default Access

The application will launch with the login screen. You'll need to create user accounts through the database initially.

## 📁 Project Structure

```
admissions-management/
├── src/
│   ├── main/
│   │   ├── java/com/example/managementadmissionwf/
│   │   │   ├── ManagementAdmissionWfApplication.java  # Main entry point
│   │   │   ├── bus/                                   # Business Logic Layer
│   │   │   │   ├── interfaces/                        # Service interfaces
│   │   │   │   └── impl/                              # Service implementations
│   │   │   ├── dal/                                   # Data Access Layer
│   │   │   │   ├── entity/                            # JPA Entities
│   │   │   │   └── repository/                        # JPA Repositories
│   │   │   ├── dto/                                   # Data Transfer Objects
│   │   │   ├── mapper/                                # MapStruct mappers
│   │   │   ├── config/                                # Configuration classes
│   │   │   ├── exception/                             # Custom exceptions
│   │   │   └── ui/                                    # Swing UI Components
│   │   │       ├── frame/                             # Main windows
│   │   │       ├── panel/                             # Content panels
│   │   │       ├── component/                         # Custom UI components
│   │   │       └── util/                              # UI utilities
│   │   └── resources/
│   │       ├── application.properties                  # Application configuration
│   │       └── icons/                                 # UI icons
│   └── test/                                          # Test code
├── markdown/                                          # Documentation
├── pom.xml                                            # Maven configuration
├── mvnw                                               # Maven wrapper (Unix)
├── mvnw.cmd                                           # Maven wrapper (Windows)
└── README.md                                          # This file
```

## 👥 User Roles

### STUDENT

- View personal profile
- Check subject scores
- Review submitted aspirations
- View admission results

### MANAGER

- Manage candidate information
- Enter and update scores
- Configure majors and subject combinations
- Set admission thresholds
- View admission lists

### ADMIN

- All MANAGER privileges
- Process admission results
- Generate statistics and reports
- System configuration

## 🗄 Database Schema

The database follows the Vietnamese University Admission System (xettuyen2026) design, organized into 3 logical layers:

### 1. MASTER DATA (DỮ LIỆU DANH MỤC)

#### xt_nganh (Ngành đào tạo)

- `idnganh`: Primary key
- `manganh`: Mã ngành (unique)
- `tennganh`: Tên ngành
- `n_tohopgoc`: Tổ hợp gốc
- `n_chitieu`: Chỉ tiêu tuyển sinh
- `n_diemsan`: Điểm sàn
- `n_diemtrungtuyen`: Điểm chuẩn
- `n_tuyenthang`: Có tuyển thẳng (boolean)
- `n_dgnl`: Có xét ĐGNL (boolean)
- `n_thpt`: Xét điểm THPT (boolean)
- `n_vsat`: Xét VSAT (boolean)
- `sl_xtt`: Số lượng tuyển thẳng
- `sl_dgnl`: Số lượng ĐGNL
- `sl_vsat`: Số lượng VSAT
- `sl_thpt`: Số lượng THPT
- `createdAt`, `updatedAt`: Timestamps

#### xt_tohop_monthi (Tổ hợp môn)

- `id`: Primary key
- `matohop`: Mã tổ hợp (A00, D01...)
- `mon1`: Môn 1 (TO, LI, HO, etc.)
- `mon2`: Môn 2
- `mon3`: Môn 3
- `tentohop`: Tên tổ hợp
- `createdAt`, `updatedAt`: Timestamps

#### xt_nganh_tohop (Mapping Ngành ↔ Tổ hợp)

- `id`: Primary key
- `manganh`: Mã ngành (FK to xt_nganh)
- `matohop`: Mã tổ hợp (FK to xt_tohop_monthi)
- `th_mon1`: Tên môn 1
- `hsmon1`: Hệ số môn 1
- `th_mon2`: Tên môn 2
- `hsmon2`: Hệ số môn 2
- `th_mon3`: Tên môn 3
- `hsmon3`: Hệ số môn 3
- `createdAt`, `updatedAt`: Timestamps

### 2. CANDIDATE DATA (DỮ LIỆU THÍ SINH)

#### xt_thisinhxettuyen25 (Thông tin thí sinh)

- `id`: Primary key
- `cccd`: CCCD (unique) - Primary identity
- `sobaodanh`: Số báo danh (unique)
- `ho`: Họ
- `ten`: Tên
- `ngay_sinh`: Ngày sinh
- `dien_thoai`: Số điện thoại
- `email`: Email
- `gioi_tinh`: Giới tính
- `noi_sinh`: Nơi sinh
- `doi_tuong`: Đối tượng ưu tiên
- `khu_vuc`: Khu vực
- `ho_va_ten`: Họ và tên đầy đủ
- `createdAt`, `updatedAt`: Timestamps

#### xt_diemthixettuyen (Điểm thi thí sinh)

- `id`: Primary key
- `cccd`: CCCD (unique) - FK to xt_thisinhxettuyen25
- `sobaodanh`: SBD (unique)
- `d_phuongthuc`: Phương thức xét tuyển (THPT/DGNL/VSAT/Tuyển thẳng)
- **Điểm các môn**: TO, LI, HO, SI, SU, DI, VA
- **Ngoại ngữ**: N1_THI (điểm thi), N1_CC (điểm chứng chỉ - max của thi và quy đổi)
- **Bài thi ĐGNL**: NL1 (Ngữ văn), NK1 (Năng lực L1), NK2 (Năng lực L2)
- `createdAt`, `updatedAt`: Timestamps

#### xt_diemcongxettuyen (Điểm cộng thêm)

- `id`: Primary key
- `cccd`: CCCD (unique) - FK to xt_thisinhxettuyen25
- `diemCC`: Điểm chứng chỉ (IELTS, SAT, etc.)
- `diemUtxt`: Điểm ưu tiên đặc biệt (HSG, thể thao, etc.)
- `diemTong`: Tổng điểm cộng = diemCC + diemUtxt
- `createdAt`, `updatedAt`: Timestamps

### 3. ADMISSION PROCESSING (XỬ LÝ XÉT TUYỂN)

#### xt_nguyenvongxettuyen (Nguyện vọng xét tuyển)

- `id`: Primary key
- `nn_cccd`: CCCD thí sinh (FK to xt_thisinhxettuyen25)
- `nv_manganh`: Mã ngành (FK to xt_nganh)
- `nv_tt`: Thứ tự nguyện vọng
- `diem_thxt`: Tổng điểm 3 môn = mon1*hs1 + mon2*hs2 + mon3*hs3
- `diem_utqd`: Điểm ưu tiên theo quy định
- `diem_cong`: Điểm cộng từ xt_diemcongxettuyen
- `diem_xettuyen`: Điểm xét tuyển cuối = diem_thxt + diem_utqd + diem_cong
- `nv_ketqua`: Kết quả (TRUNG_TUYEN, TRUOT, CHO_XET)
- `createdAt`, `updatedAt`: Timestamps

#### xt_bangquydoi (Bảng quy đổi điểm)

- `id`: Primary key
- `d_phuongthuc`: Phương thức xét tuyển (THPT, DGNL, VSAT)
- `d_tohop`: Tổ hợp (A00, D01, etc.)
- `d_mon`: Môn (TO, LI, N1, NL1, etc.)
- `d_diema`: Điểm A (điểm thấp nhất trong khoảng)
- `d_diemb`: Điểm B (điểm cao nhất trong khoảng)
- `d_diemc`: Điểm C (điểm quy đổi tương ứng)
- `d_diemd`: Điểm D (điểm quy đổi tối đa - optional)
- `createdAt`, `updatedAt`: Timestamps

### Legacy Entities (Preserved for compatibility)

#### Users

- `id`: Primary key
- `fullname`: User's full name
- `email`: Email address (unique)
- `username`: Login username (unique)
- `password`: Encrypted password
- `role`: User role (STUDENT, MANAGER, ADMIN)
- `createdAt`, `updatedAt`: Timestamps

#### Students (Legacy - use xt_thisinhxettuyen25 instead)

- `id`: Primary key
- `user_id`: Foreign key to Users
- `address`: Student address
- `school`: Previous school
- `score`: Total score
- `additionScore`: Additional score
- `totalAspiration`: Number of aspirations
- `createdAt`, `updatedAt`: Timestamps

## 💻 Development

### Code Style

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Implement proper exception handling
- Write clear, self-documenting code

### Adding New Features

1. Create/update JPA entities in `dal/entity/`
2. Create/update repositories in `dal/repository/`
3. Create/update DTOs in `dto/`
4. Create/update mappers in `mapper/`
5. Implement business logic in `bus/`
6. Create UI panels in `ui/panel/`
7. Add navigation in `ui/component/Navigation.java`

### Hot Reload

The application includes Spring DevTools for hot reloading during development:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

## 🧪 Testing

Run tests using Maven:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ManagementAdmissionWfApplicationTests
```

### Test Dependencies

- `spring-boot-starter-security-test`: Security testing
- `spring-boot-starter-validation-test`: Validation testing
- `spring-boot-starter-security-oauth2-authorization-server-test`: OAuth2 testing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions:

- Open an issue on GitHub
- Contact the development team

## 🔮 Future Enhancements

- [ ] Complete all service implementations
- [ ] Add comprehensive unit and integration tests
- [ ] Implement data export functionality (Excel, PDF)
- [ ] Add backup and restore features
- [ ] Implement batch score import
- [ ] Add audit logging
- [ ] Create installation and deployment scripts
- [ ] Add user management interface
- [ ] Implement real-time notifications
- [ ] Add data visualization charts

## 📚 Additional Documentation

- See [ARCHITECTURE.md](markdown/architecture.md) for detailed technical architecture
- See [dbDesign.md](markdown/dbDesign.md) for database design requirements
- See inline code comments for implementation details

## ⚠️ Migration Notice

The system is transitioning from legacy entities to new Xt* entities following Vietnamese Ministry of Education standards:

**Deprecated Entities** (will be removed):

- `Students` → Use `XtThisinhxettuyen25`
- `Major` → Use `XtNganh`
- `MajorDetail` → Use `XtNganhTohop`
- `ComplexSubject` → Use `XtTohopMonthi`
- `Subject` → Embedded in `XtTohopMonthi`
- `StudentSubject` → Use `XtDiemthixettuyen`
- `Aspiration` → Use `XtNguyenvongxettuyen`

**Kept Entities**:

- `Users` - Authentication and user management
- `RoleUser` - Role definitions

For detailed migration instructions, see [MIGRATION_GUIDE.md](markdown/MIGRATION_GUIDE.md).

---

**Built with ❤️ using Java, Spring Boot, and Swing**
