# Thymeleaf Web Application

Một ví dụ best practices cho ứng dụng web Thymeleaf sử dụng Spring Boot MVC architecture.

## 📋 Tổng Quan

Dự án này minh họa cách xây dựng ứng dụng web maintainable, scalable và dễ mở rộng theo mô hình MVC (Model-View-Controller) với các công nghệ:

- **Spring Boot 4.0.6** - Framework chính
- **Spring MVC** - Web framework
- **Thymeleaf** - Template engine
- **Spring Security** - Security framework
- **H2 Database** - Database (development)

## ✨ Tính Năng

- ✅ **MVC Architecture** - Phân tách rõ ràng Controller, Service, Repository
- ✅ **Clean Code** - Tuân thủ SOLID principles, dễ đọc và maintain
- ✅ **Exception Handling** - Global exception handler với error pages tùy chỉnh
- ✅ **Spring Security** - Security framework (đã config để không yêu cầu authentication)
- ✅ **Responsive Design** - Giao diện Bootstrap 5, hỗ trợ mobile
- ✅ **Reusable Components** - Layout templates, fragments
- ✅ **Best Practices** - Coding standards, testing guidelines

## 🏗️ Cấu Trúc Dự Án

```
thymeleaf_web/
├── src/
│   ├── main/
│   │   ├── java/com/example/thymeleaf_web/
│   │   │   ├── config/              # Cấu hình Spring Boot
│   │   │   ├── controller/          # Controllers (MVC - C)
│   │   │   ├── service/             # Business Logic (MVC - M)
│   │   │   ├── repository/           # Data Access (MVC - M)
│   │   │   ├── model/               # Entities, DTOs, Enums
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   └── util/               # Utility Classes
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf Templates (MVC - V)
│   │       ├── static/              # CSS, JS, Images
│   │       └── application.properties
│   └── test/                       # Unit & Integration Tests
├── docs/                           # Documentation
│   ├── architecture/               # Architecture docs
│   ├── guides/                    # Development guides
│   └── operation-manual/           # Operational procedures
└── README.md
```

## 🚀 Bắt Đầu Nhanh

### Yêu Cầu

- Java 21+
- Maven 3.6+
- Git

### Cài Đặt

1. **Clone repository**
```bash
git clone <repository-url>
cd thymeleaf_web
```

2. **Build project**
```bash
mvn clean install
```

3. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

4. **Truy cập ứng dụng**
```
http://localhost:8080
```

### H2 Console (Development)

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave empty)
```

## 📚 Tài Liệu

### Architecture Documentation
- [Cấu Trúc MVC Chi Tiết](docs/architecture/mvc-structure.md) - Cấu trúc thư mục và quy tắc
- [Hướng Dẫn Vận Hành](docs/operation-manual/operational-procedures.md) - Quy trình vận hành chi tiết

### Development Guides
- [Thêm Tính Năng Mới](docs/guides/adding-new-feature.md) - Hướng dẫn phát triển
- [Exception Handling](docs/guides/exception-handling.md) - Xử lý lỗi

## 🔧 Cấu Hình

### Application Properties

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Thymeleaf
spring.thymeleaf.cache=false

# H2 Console
spring.h2.console.enabled=true
```

## 🎯 Quy Trình Phát Triển

### 1. Thêm Tính Năng Mới

```
1. Tạo Entity (model/entity/)
2. Tạo Repository (repository/)
3. Tạo DTOs (model/dto/)
4. Tạo Service Interface & Implementation (service/)
5. Tạo Controller (controller/)
6. Tạo Thymeleaf Templates (templates/)
7. Tạo Tests (test/)
```

### 2. Coding Standards

- ✅ Constructor Injection
- ✅ @Transactional trên Service methods
- ✅ Proper exception handling
- ✅ Validation using Bean Validation
- ✅ Meaningful names
- ✅ Javadoc comments
- ✅ File size < 300 lines

Xem chi tiết tại: [Hướng Dẫn Vận Hành](docs/operation-manual/operational-procedures.md)

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Test Structure
```
src/test/java/com/example/thymeleaf_web/
├── controller/          # Controller tests
├── service/            # Service layer tests
└── integration/        # Integration tests
```

## 📦 Deployment

### Build JAR
```bash
mvn clean package -DskipTests
```

### Run JAR
```bash
java -jar target/thymeleaf_web-0.0.1-SNAPSHOT.jar
```

### With Profile
```bash
java -jar target/thymeleaf_web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🔒 Security

- Authentication với Spring Security
- Role-based access control
- Protected endpoints
- CSRF protection (có thể enable trong production)

Xem: [SecurityConfig.java](src/main/java/com/example/thymeleaf_web/config/SecurityConfig.java)

## 🌟 Best Practices

### Code Quality
- ✅ SOLID Principles
- ✅ DRY (Don't Repeat Yourself)
- ✅ KISS (Keep It Simple, Stupid)
- ✅ Clean Code standards

### Architecture
- ✅ Separation of Concerns
- ✅ Layered Architecture
- ✅ Dependency Injection
- ✅ Loose Coupling

### Performance
- ✅ Connection pooling
- ✅ Lazy loading
- ✅ Pagination
- ✅ Caching strategy

## 📖 Tài Liệu Tham Khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [MVC Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

## 🤝 Đóng Góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 License

Dự án này được cấp phép theo MIT License - xem file [LICENSE](LICENSE) để biết chi tiết.

## 👨‍💻 Team

- Development Team

## 📞 Hỗ Trợ

Nếu có câu hỏi hoặc vấn đề, hãy mở issue trên GitHub.

---

**Made with ❤️ using Spring Boot & Thymeleaf**