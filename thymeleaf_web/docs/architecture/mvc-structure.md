# Cấu trúc MVC chuẩn cho Thymeleaf Web Application

## Tổng quan

Tài liệu này mô tả cấu trúc chuẩn theo mô hình MVC (Model-View-Controller) cho dự án Thymeleaf Web, đảm bảo:
- ✅ Dễ maintain
- ✅ Dễ bảo trì và mở rộng
- ✅ Tuân thủ SOLID principles
- ✅ Phân tách trách nhiệm rõ ràng

## Cấu trúc thư mục chi tiết

```
thymeleaf_web/
├── src/
│   ├── main/
│   │   ├── java/com/example/thymeleaf_web/
│   │   │   ├── config/                     # Configuration classes
│   │   │   │   ├── SecurityConfig.java     # Spring Security configuration
│   │   │   │   ├── ThymeleafConfig.java   # Thymeleaf customization
│   │   │   │   └── WebMvcConfig.java       # Web MVC configuration
│   │   │   ├── controller/                 # Controllers (MVC - C)
│   │   │   │   ├── advice/                # Exception handling
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── HomeController.java
│   │   │   │   └── user/                  # Feature-based controllers
│   │   │   │       └── UserController.java
│   │   │   ├── service/                   # Business logic (MVC - M - Service Layer)
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/
│   │   │   │       └── UserServiceImpl.java
│   │   │   ├── repository/                # Data access (MVC - M - Repository Layer)
│   │   │   │   └── UserRepository.java
│   │   │   ├── model/                     # Domain models & DTOs
│   │   │   │   ├── entity/                # JPA Entities
│   │   │   │   │   └── User.java
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   │   ├── request/           # Request DTOs
│   │   │   │   │   │   └── UserCreateRequest.java
│   │   │   │   │   └── response/          # Response DTOs
│   │   │   │   │       └── UserResponse.java
│   │   │   │   └── enums/                 # Enumerations
│   │   │   ├── validator/                 # Custom validators
│   │   │   │   └── CustomValidators.java
│   │   │   ├── exception/                # Custom exceptions
│   │   │   │   ├── BusinessException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── util/                     # Utility classes
│   │   │   │   ├── DateUtils.java
│   │   │   │   └── StringUtils.java
│   │   │   └── ThymeleafWebApplication.java
│   │   └── resources/
│   │       ├── application.properties     # Main configuration
│   │       ├── application-dev.properties # Dev environment
│   │       ├── application-prod.properties # Prod environment
│   │       ├── templates/                 # Thymeleaf templates (MVC - V)
│   │       │   ├── layout/                # Layout templates
│   │       │   │   ├── main.html          # Main layout
│   │       │   │   ├── header.html        # Header fragment
│   │       │   │   └── footer.html        # Footer fragment
│   │       │   ├── fragments/             # Reusable fragments
│   │       │   │   ├── alert.html
│   │       │   │   └── pagination.html
│   │       │   ├── errors/                # Error pages
│   │       │   │   ├── 404.html
│   │       │   │   └── 500.html
│   │       │   ├── index.html
│   │       │   └── user/
│   │       │       ├── list.html
│   │       │       ├── form.html
│   │       │       └── detail.html
│   │       ├── static/                   # Static resources
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── js/
│   │       │   │   └── main.js
│   │       │   └── images/
│   │       ├── messages/                  # i18n messages
│   │       │   ├── messages.properties    # Default (English)
│   │       │   └── messages_vi.properties # Vietnamese
│   │       └── db/                       # Database scripts
│   │           ├── schema.sql
│   │           └── data.sql
│   └── test/                             # Test directory
│       └── java/com/example/thymeleaf_web/
│           ├── controller/
│           │   └── UserControllerTest.java
│           ├── service/
│           │   └── UserServiceTest.java
│           └── integration/
│               └── WebIntegrationTest.java
├── docs/                                # Documentation
│   ├── architecture/
│   │   └── mvc-structure.md            # This file
│   ├── guides/
│   │   ├── adding-new-feature.md
│   │   └── exception-handling.md
│   └── operation-manual/
│       └── operational-procedures.md
├── .rules/                              # AI rules (optional)
│   ├── rules.md
│   └── backend-rules.md
├── pom.xml
└── README.md
```

## Quy tắc đặt tên và cấu trúc

### 1. Package Structure

- **Config**: Chứa các class configuration cho Spring Boot
- **Controller**: Xử lý HTTP requests, mapping tới views
- **Service**: Chứa business logic, giao tiếp giữa Controller và Repository
- **Repository**: Data access layer, giao tiếp với database
- **Model**: Chứa entities, DTOs, enums
  - `entity`: JPA entities
  - `dto/request`: DTOs cho request body
  - `dto/response`: DTOs cho response body
- **Validator**: Custom validation logic
- **Exception**: Custom exceptions
- **Util**: Helper/utility methods

### 2. Naming Conventions

#### Controllers
- Sử dụng hậu tố `Controller`
- Ví dụ: `UserController`, `HomeController`

#### Services
- Interface: `[Name]Service`
- Implementation: `[Name]ServiceImpl`
- Ví dụ: `UserService`, `UserServiceImpl`

#### Repositories
- Interface: `[Name]Repository`
- Kế thừa từ `JpaRepository` hoặc `CrudRepository`
- Ví dụ: `UserRepository`

#### DTOs
- Request: `[Action][Entity]Request` (ví dụ: `UserCreateRequest`)
- Response: `[Entity]Response` (ví dụ: `UserResponse`)

### 3. Layer Responsibilities

#### Controller Layer
- ✅ Nhận HTTP requests
- ✅ Validate input (sử dụng @Valid)
- ✅ Gọi Service layer
- ✅ Trả về View names hoặc Model objects
- ❌ KHÔNG chứa business logic
- ❌ KHÔNG trực tiếp access database

#### Service Layer
- ✅ Chứa business logic
- ✅ Gọi Repository layer
- ✅ Xử lý transactions (@Transactional)
- ❌ KHÔNG biết về HTTP requests/responses
- ❌ KHÔNG trực tiếp access to View layer

#### Repository Layer
- ✅ Data access operations
- ✅ Query definitions
- ❌ KHÔNG chứa business logic

### 4. Templates Structure

- **layout**: Layout templates dùng chung
- **fragments**: Reusable components (header, footer, pagination)
- **errors**: Custom error pages (404, 500, etc.)
- **[feature]**: Templates theo từng feature

### 5. Static Resources

- **css**: Stylesheets
- **js**: JavaScript files
- **images**: Images and icons
- **fonts**: Custom fonts

## Best Practices

### 1. Dependency Injection
- Sử dụng Constructor Injection thay vì Field Injection
- Mark constructor hoặc class với `@RequiredArgsConstructor` (Lombok)

### 2. Transaction Management
- Annotate Service methods với `@Transactional`
- Set `readOnly = true` cho các method chỉ đọc dữ liệu

### 3. Exception Handling
- Tạo custom exceptions cho business logic
- Sử dụng `@ControllerAdvice` để xử lý exceptions globally

### 4. Validation
- Sử dụng Bean Validation (javax.validation)
- Tạo custom validators khi cần logic phức tạp

### 5. Code Organization
- Mỗi class nên có trách nhiệm đơn nhất (Single Responsibility)
- Package theo feature hoặc layer (không trộn lẫn)
- File size nhỏ (< 300 lines)

### 6. Security
- Validate tất cả inputs
- Không expose internal details ra error messages
- Sử dụng HTTPS trong production
- Implement proper authentication & authorization

## Phát triển tính năng mới

Xem tài liệu chi tiết tại `docs/guides/adding-new-feature.md`

Tóm tắt:
1. Tạo Entity (nếu cần)
2. Tạo Repository
3. Tạo DTOs (Request/Response)
4. Tạo Service Interface & Implementation
5. Tạo Controller
6. Tạo Thymeleaf templates
7. Tạo tests
8. Cập nhật documentation

## Testing

- **Unit Tests**: Test Service layer độc lập
- **Integration Tests**: Test Controller với Spring MVC Test
- **End-to-End Tests**: Test toàn bộ flow với Selenium (optional)

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)