# Hướng Dẫn Vận Hành - Thymeleaf Web Application

## Table of Contents
1. [Giới Thiệu](#giới-thiệu)
2. [Kiến Trúc Hệ Thống](#kiến-trúc-hệ-thống)
3. [Quy Trình Vận Hành](#quy-trình-vận-hành)
4. [Quy Trình Phát Triển](#quy-trình-phát-triển)
5. [Quy Trình Triển Khai](#quy-trình-triển-khai)
6. [Quy Trình Bảo Trì](#quy-trình-bảo-trì)
7. [Quy Trình Mở Rộng](#quy-trình-mở-rộng)
8. [Quy Trình Xử Lý Sự Cố](#quy-trình-xử-lý-sự-cố)
9. [Best Practices](#best-practices)

---

## Giới Thiệu

### Mục Đích
Tài liệu này cung cấp hướng dẫn chi tiết về quy trình vận hành, phát triển, bảo trì và mở rộng cho ứng dụng Thymeleaf Web được xây dựng theo mô hình MVC chuẩn.

### Phạm Vi
- Kiến trúc hệ thống và thành phần
- Quy trình vận hành hàng ngày
- Quy trình phát triển tính năng mới
- Quy trình triển khai (deployment)
- Quy trình bảo trì và khắc phục sự cố
- Quy trình mở rộng hệ thống

---

## Kiến Trúc Hệ Thống

### Tổng Quan Kiến Trúc MVC

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ HTTP Request
       ▼
┌─────────────────────────────────────┐
│        Controller Layer            │  ──► Xử lý HTTP Request
│  - Nhận Request                   │      Validate Input
│  - Validate dữ liệu               │      Gọi Service Layer
│  - Gọi Service                    │      Trả về View
│  - Trả về View                    │
└─────────────┬─────────────────────┘
              │
              │ Business Logic Call
              ▼
┌─────────────────────────────────────┐
│        Service Layer               │  ──► Business Logic
│  - Xử lý nghiệp vụ                │      Transaction Management
│  - Gọi Repository                 │      Validation Rules
│  - Trả về dữ liệu                 │      Business Rules
└─────────────┬─────────────────────┘
              │
              │ Data Access Call
              ▼
┌─────────────────────────────────────┐
│      Repository Layer              │  ──► Data Access
│  - Query Database                  │      CRUD Operations
│  - Map Entity                     │      Data Mapping
│  - Trả về Entity                  │
└─────────────┬─────────────────────┘
              │
              │ SQL Query
              ▼
┌─────────────────────────────────────┐
│        Database                    │  ──► Data Storage
│  - H2 / MySQL / PostgreSQL         │      Persistent Data
└─────────────────────────────────────┘
```

### Thành Phần Hệ Thống

#### 1. Controller Layer (src/main/java/.../controller/)
- **Trách nhiệm**: Xử lý HTTP requests, điều hướng đến views
- **Công cụ**: Spring MVC annotations (@Controller, @GetMapping, @PostMapping)
- **Quy tắc**: Không chứa business logic, chỉ điều phối

#### 2. Service Layer (src/main/java/.../service/)
- **Trách nhiệm**: Xử lý business logic, giao tiếp giữa Controller và Repository
- **Công cụ**: @Service, @Transactional
- **Quy tắc**: Pure business logic, không phụ thuộc web

#### 3. Repository Layer (src/main/java/.../repository/)
- **Trách nhiệm**: Data access, giao tiếp với database
- **Công cụ**: Spring Data JPA, JPA Repositories
- **Quy tắc**: Chỉ CRUD operations, không business logic

#### 4. View Layer (src/main/resources/templates/)
- **Trách nhiệm**: Hiển thị giao diện người dùng
- **Công cụ**: Thymeleaf templates
- **Quy tắc**: Logic tối thiểu, chỉ hiển thị

#### 5. Model Layer (src/main/java/.../model/)
- **Entity**: JPA entities - ánh xạ database
- **DTO**: Data Transfer Objects - truyền dữ liệu giữa layers
- **Enum**: Các giá trị hằng số

---

## Quy Trình Vận Hành

### 1. Quy Trình Xử Lý Request

#### Flow Chi Tiết:

```
User → HTTP Request → Security Filter → Controller → 
Service → Repository → Database → Repository → 
Service → Controller → Thymeleaf Template Engine → 
HTML Response → User Browser
```

#### Bước 1: Request Nhận Từ Client
- User gửi HTTP request (GET, POST, PUT, DELETE)
- Request đi qua Security Filter Chain
- Authentication & Authorization được kiểm tra

#### Bước 2: Controller Xử Lý
- Controller nhận request và parameters
- Validate input sử dụng @Valid
- Chuẩn bị DTO từ request data
- Gọi Service layer

#### Bước 3: Service Layer Xử Lý
- Nhận DTO từ Controller
- Thực hiện business logic
- Validate business rules
- Gọi Repository để access data
- Trả về DTO response

#### Bước 4: Repository Access Data
- Thực hiện database query
- Map results sang Entities
- Trả về Entities cho Service
- Service chuyển đổi sang DTOs

#### Bước 5: View Rendering
- Controller nhận DTOs
- Thêm vào Model object
- Trả về view name
- Thymeleaf render template với data
- Trả về HTML response

### 2. Quy Trình Xử Lý Error

```
Exception Occurs → GlobalExceptionHandler → 
Determine Exception Type → 
Select Error Template → 
Render Error Page → 
Send to Client
```

#### Error Handling Hierarchy:
1. **ResourceNotFoundException** → 404 Not Found
2. **BusinessException** → 500 Internal Server Error
3. **AccessDeniedException** → 403 Forbidden
4. **Other Exceptions** → 500 Internal Server Error

---

## Quy Trình Phát Triển

### 1. Thiết Kế Tính Năng Mới

#### Step 1: Phân Tích Yêu Cầu
- [ ] Xác định business requirements
- [ ] Định nghĩa user stories
- [ ] Thiết kế database schema (nếu cần)
- [ ] Thiết kế API endpoints
- [ ] Thiết kế UI wireframes

#### Step 2: Tạo Database Schema (nếu cần)
- [ ] Thiết kế tables và relationships
- [ ] Viết migration scripts
- [ ] Test schema locally
- [ ] Document schema changes

#### Step 3: Implementation Order
1. **Entity Classes** (model/entity/)
2. **Repository Interfaces** (repository/)
3. **DTO Classes** (model/dto/)
4. **Service Interface & Implementation** (service/)
5. **Controller** (controller/)
6. **Thymeleaf Templates** (templates/)
7. **Static Resources** (static/)
8. **Tests** (test/)

### 2. Coding Standards

#### File Naming:
- Controllers: `*Controller.java`
- Services: `*Service.java`, `*ServiceImpl.java`
- Repositories: `*Repository.java`
- DTOs: `*Request.java`, `*Response.java`
- Entities: `*.java`

#### Package Structure:
```
com.example.thymeleaf_web
├── config
├── controller
│   └── advice
├── service
│   └── impl
├── repository
├── model
│   ├── entity
│   ├── dto
│   │   ├── request
│   │   └── response
│   └── enums
├── exception
├── util
└── validator
```

#### Code Quality Checklist:
- [ ] Constructor Injection thay vì Field Injection
- [ ] @Transactional trên Service methods
- [ ] Proper exception handling
- [ ] Validation using Bean Validation
- [ ] Meaningful variable/method names
- [ ] Javadoc comments on public methods
- [ ] File size < 300 lines

### 3. Testing Strategy

#### Unit Tests (Service Layer):
```java
@Test
void testMethodName_ExpectedResult_WhenCondition() {
    // Arrange
    // Act
    // Assert
}
```

#### Integration Tests (Controller Layer):
```java
@WebMvcTest(Controller.class)
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testEndpoint_ReturnsExpectedView() throws Exception {
        mockMvc.perform(get("/endpoint"))
            .andExpect(status().isOk())
            .andExpect(view().name("expected-view"));
    }
}
```

---

## Quy Trình Triển Khai

### 1. Environment Setup

#### Development Environment:
```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.thymeleaf.cache=false
logging.level.com.example.thymeleaf_web=DEBUG
```

#### Production Environment:
```properties
# application-prod.properties
spring.datasource.url=jdbc:mysql://prod-db:3306/appdb
spring.thymeleaf.cache=true
logging.level.com.example.thymeleaf_web=INFO
```

### 2. Build & Deploy Steps

#### Step 1: Build Application
```bash
# Clean and build
mvn clean package -DskipTests

# Or with tests
mvn clean package
```

#### Step 2: Run Application
```bash
# Run JAR file
java -jar target/thymeleaf_web-0.0.1-SNAPSHOT.jar

# Or with specific profile
java -jar target/thymeleaf_web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

#### Step 3: Health Check
```bash
# Check if application is running
curl http://localhost:8080/actuator/health
```

### 3. Deployment Checklist

#### Pre-Deployment:
- [ ] All tests pass
- [ ] Code review approved
- [ ] Database migrations applied
- [ ] Environment variables configured
- [ ] Backup taken

#### Post-Deployment:
- [ ] Health check successful
- [ ] Smoke tests pass
- [ ] Monitor for errors
- [ ] Verify critical functionality

---

## Quy Trình Bảo Trì

### 1. Daily Maintenance Tasks

#### Monitoring:
- [ ] Check application logs for errors
- [ ] Monitor database performance
- [ ] Check disk space
- [ ] Monitor memory usage
- [ ] Review security logs

#### Backup:
- [ ] Daily database backup
- [ ] Backup application configuration
- [ ] Verify backup integrity
- [ ] Store backups off-site

### 2. Weekly Maintenance Tasks

#### Updates:
- [ ] Review and apply security patches
- [ ] Update dependencies (carefully)
- [ ] Review logs for patterns
- [ ] Performance analysis

#### Cleanup:
- [ ] Archive old logs
- [ ] Cleanup temporary files
- [ ] Database maintenance (index rebuild, etc.)

### 3. Monthly Maintenance Tasks

#### Review:
- [ ] Performance metrics review
- [ ] Security audit
- [ ] Capacity planning
- [ ] Disaster recovery drill

---

## Quy Trình Mở Rộng

### 1. Adding New Features

#### Step-by-Step Process:

**Step 1: Planning**
```
1. Define feature requirements
2. Design database changes (if any)
3. Design API endpoints
4. Design UI mockups
5. Plan testing strategy
```

**Step 2: Implementation**
```
1. Create Entity (if new table needed)
2. Create Repository
3. Create DTOs (Request/Response)
4. Create Service Interface
5. Create Service Implementation
6. Create Controller
7. Create Thymeleaf Templates
8. Write Tests
```

**Step 3: Integration**
```
1. Update navigation (if needed)
2. Add permissions (if needed)
3. Update documentation
4. Code review
5. Deploy to staging
6. UAT (User Acceptance Testing)
7. Deploy to production
```

### 2. Scaling Strategies

#### Horizontal Scaling:
- Add more application instances
- Use load balancer (Nginx, HAProxy)
- Session clustering or sticky sessions

#### Vertical Scaling:
- Increase CPU resources
- Add more memory
- Optimize database queries

#### Database Scaling:
- Read replicas for read-heavy workloads
- Database sharding for large datasets
- Caching layer (Redis, Memcached)

---

## Quy Trình Xử Lý Sự Cố

### 1. Incident Classification

#### Severity Levels:
- **P1 - Critical**: Complete system outage
- **P2 - High**: Major functionality broken
- **P3 - Medium**: Partial functionality affected
- **P4 - Low**: Minor issues, workarounds available

### 2. Incident Response Process

#### Step 1: Detection & Acknowledgment
```
1. Monitor alerts trigger
2. On-call engineer acknowledges
3. Assess severity level
4. Notify stakeholders (if P1/P2)
```

#### Step 2: Investigation
```
1. Check application logs
2. Check system metrics
3. Reproduce issue (if possible)
4. Identify root cause
```

#### Step 3: Resolution
```
1. Implement fix
2. Test fix in staging
3. Deploy to production
4. Verify resolution
```

#### Step 4: Post-Incident
```
1. Write incident report
2. Root cause analysis
3. Identify preventive measures
4. Update documentation
5. Schedule follow-up review
```

### 3. Common Issues & Solutions

#### Issue: Application Won't Start
**Possible Causes:**
- Port already in use
- Database connection failed
- Configuration error

**Solutions:**
```bash
# Check port usage
netstat -ano | findstr :8080

# Check database connection
# Verify connection string
# Check database credentials
```

#### Issue: 500 Internal Server Error
**Possible Causes:**
- Unhandled exception
- Null pointer exception
- Database query error

**Solutions:**
```java
// Check application logs
// Review GlobalExceptionHandler
// Verify Service logic
// Check Repository queries
```

#### Issue: Memory Leaks
**Possible Causes:**
- Unreleased resources
- Large objects in memory
- Connection leaks

**Solutions:**
```java
// Use try-with-resources
// Implement proper cleanup
// Monitor heap size
// Use profiling tools
```

---

## Best Practices

### 1. Code Quality

#### Write Clean Code:
- [ ] Use meaningful names
- [ ] Keep methods short (< 20 lines)
- [ ] Follow DRY (Don't Repeat Yourself)
- [ ] Single Responsibility Principle
- [ ] Avoid nested code (max 3 levels)

#### Documentation:
- [ ] Javadoc on public methods
- [ ] Comment complex logic
- [ ] Keep README up to date
- [ ] Document API endpoints

### 2. Security

#### Input Validation:
- [ ] Always validate user input
- [ ] Use Bean Validation
- [ ] Sanitize output (prevent XSS)
- [ ] Use parameterized queries (prevent SQLi)

#### Authentication & Authorization:
- [ ] Implement proper authentication
- [ ] Use role-based access control
- [ ] Protect sensitive endpoints
- [ ] Use HTTPS in production

### 3. Performance

#### Optimization:
- [ ] Use database indexes
- [ ] Implement caching
- [ ] Optimize queries
- [ ] Lazy loading for relationships
- [ ] Pagination for large datasets

#### Monitoring:
- [ ] Track response times
- [ ] Monitor error rates
- [ ] Set up alerts
- [ ] Regular performance reviews

### 4. Scalability

#### Design for Scale:
- [ ] Stateless application design
- [ ] Use connection pooling
- [ ] Implement caching strategy
- [ ] Design for horizontal scaling

---

## Kết Luận

Tài liệu này cung cấp hướng dẫn toàn diện cho việc vận hành, phát triển, bảo trì và mở rộng ứng dụng Thymeleaf Web. Tuân thủ các quy trình và best practices này sẽ đảm bảo:

✅ **Dễ maintain**: Code structure rõ ràng, dễ đọc, dễ hiểu
✅ **Dễ bảo trì**: Modular design, clear separation of concerns
✅ **Dễ mở rộng**: Scalable architecture, flexible design patterns
✅ **High quality**: Code standards, testing, documentation

### Additional Resources:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Project Repository Structure](mvc-structure.md)

---

**Last Updated**: 2024
**Version**: 1.0
**Maintained By**: Development Team