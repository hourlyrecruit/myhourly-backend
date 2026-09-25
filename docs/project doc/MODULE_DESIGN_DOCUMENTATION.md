# MyHourly — HRMS Module Design Documentation

> **Application**: MyHourly — Human Resource Management System (HRMS)
> **Version**: 1.0.0
> **Document Type**: Module Design Documentation

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture Overview](#2-architecture-overview)
3. [Cross-Cutting Concerns](#3-cross-cutting-concerns)
4. [Module 1 — Authentication](#4-module-1--authentication)
5. [Module 2 — Security](#5-module-2--security)
6. [Module 3 — Master Data](#6-module-3--master-data)
7. [Module 4 — Employee](#7-module-4--employee)
8. [Module 5 — Attendance](#8-module-5--attendance)
9. [Module 6 — Leave](#9-module-6--leave)
10. [Module 7 — Holiday](#10-module-7--holiday)
11. [Module 8 — Payroll](#11-module-8--payroll)
12. [Module 9 — Performance](#12-module-9--performance)
13. [Module 10 — Report](#13-module-10--report)
14. [Module 11 — Settings](#14-module-11--settings)
15. [Module 12 — Calendar](#15-module-12--calendar)
16. [Module 13 — Notification](#16-module-13--notification)
17. [Module 14 — Project (Stub)](#17-module-14--project-stub)
18. [Module 15 — Timesheet (Stub)](#18-module-15--timesheet-stub)
19. [Module 16 — Common](#19-module-16--common)
20. [Module 17 — Seed](#20-module-17--seed)
21. [Module 18 — Audit](#21-module-18--audit)
22. [Module 19 — Lookup](#22-module-19--lookup)
23. [Module 20 — Util](#23-module-20--util)
24. [Module Dependency Graph](#24-module-dependency-graph)
25. [Security & Access Control Matrix](#25-security--access-control-matrix)
26. [Design Patterns Summary](#26-design-patterns-summary)

---

## 1. Project Overview

**MyHourly** is a comprehensive Human Resource Management System (HRMS) built on the Spring Boot ecosystem. It provides end-to-end HR operations including employee management, attendance tracking, leave management, payroll processing, performance reviews, reporting, and notifications.

### 1.1 Technology Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 4.0.7 |
| Language | Java 21 |
| Persistence | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT |
| Email | Spring Mail |
| Database | PostgreSQL / MySQL |
| API Documentation | SpringDoc OpenAPI (Swagger) |
| File Storage | Cloudinary / Cloudflare R2 |
| PDF Generation | PDF library (Payslip / Report exporters) |
| Excel Export | Apache POI (Excel exporters) |

### 1.2 Key Conventions

- **API Base Path**: `/api/v1/`
- **Base Entity**: All entities extend `BaseEntity` (`id`, `createdAt`, `updatedAt`)
- **Layered Architecture**: Controller → Service → Repository → Entity
- **DTO Pattern**: Request/Response objects decouple the API contract from the persistence model
- **Mapper Pattern**: Dedicated mapper classes convert between entities and DTOs
- **Specification Pattern**: Dynamic query building via Spring Data JPA Specifications

### 1.3 Roles (RBAC)

| Role | Description |
|------|-------------|
| `SUPER_ADMIN` | Full system administration |
| `HR_ADMIN` | Human resource administration |
| `MANAGER` | Team / department management |
| `EMPLOYEE` | Standard employee access |
| `PAYROLL_ADMIN` | Payroll processing |
| `CLIENT` | External client access |

---

## 2. Architecture Overview

### 2.1 Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Presentation                         │
│              Controllers (REST /api/v1/...)                 │
├─────────────────────────────────────────────────────────────┤
│                         Application                         │
│              Services + Mappers + Specifications            │
├─────────────────────────────────────────────────────────────┤
│                         Persistence                         │
│              Repositories (Spring Data JPA)                 │
├─────────────────────────────────────────────────────────────┤
│                            Domain                           │
│              Entities + Enums (extend BaseEntity)           │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Request Flow

```
Client
  │  HTTP Request (JWT Bearer Token)
  ▼
JwtAuthenticationFilter ──► validates token, sets SecurityContext
  ▼
Controller (validates DTO)
  ▼
Service (business logic, transactions)
  ▼
Repository (Spring Data JPA)
  ▼
Database (PostgreSQL / MySQL)
```

### 2.3 Package Structure Convention

Each functional module follows a consistent package layout:

```
com.my_hourly.<module>
├── api/
│   ├── controller/     # REST controllers
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── entity/             # JPA entities
├── enums/              # Enum types
├── mapper/             # Entity ↔ DTO mappers
├── repository/         # Spring Data repositories
├── service/            # Service interfaces
│   └── impl/           # Service implementations
├── specification/      # JPA Specifications
├── scheduler/          # Scheduled jobs
└── util/               # Module-specific utilities
```

---

## 3. Cross-Cutting Concerns

### 3.1 Base Entity

All entities extend `BaseEntity` located in the Common module:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### 3.2 Standard API Response Envelope

All endpoints return a standardized response envelope:

```json
{
  "success": true,
  "message": "Operation completed",
  "data": { },
  "timestamp": "2026-08-02T16:00:00Z"
}
```

### 3.3 Error Handling

Centralized exception handling via `GlobalExceptionHandler` in the Common module. Custom exceptions include:

- `ResourceNotFoundException`
- `BadRequestException`
- `BusinessException`
- `DuplicateResourceException`
- `ForbiddenException`
- `UnauthorizedException`
- `ValidationException`

---

## 4. Module 1 — Authentication

**Package**: `com.my_hourly.authentication`

### 4.1 Overview & Purpose

Handles user identity, credential management, token lifecycle, and administrative user operations. It is the entry point for all authenticated access to the system.

### 4.2 Package Structure

```
com.my_hourly.authentication
├── api/
│   ├── controller/
│   │   ├── AuthenticationController.java
│   │   └── AdminController.java
│   ├── request/    # LoginRequest, RefreshTokenRequest, ChangePasswordRequest,
│   │               # ForgotPasswordRequest, ResetPasswordRequest, AdminRegisterRequest,
│   │               # GrantRoleRequest, UpdateUserStatusRequest, EmployeeRegisterRequest
│   └── response/   # LoginResponse, RefreshTokenResponse, RegisterResponse, UserProfileResponse
├── entity/
│   ├── User.java
│   ├── RefreshToken.java
│   ├── RevokedToken.java
│   ├── PasswordResetToken.java
│   ├── RoleName.java
│   └── UserStatus.java
├── mapper/
│   ├── AuthenticationMapper.java
│   └── UserMapper.java
├── repository/
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   ├── RevokedTokenRepository.java
│   └── PasswordResetTokenRepository.java
└── service/
    ├── AuthenticationService.java
    ├── AdminService.java
    ├── PasswordResetEmailService.java
    └── impl/
        ├── AuthenticationServiceImpl.java
        ├── AdminServiceImpl.java
        └── PasswordResetEmailServiceImpl.java
```

### 4.3 Entity Relationship Diagram

```
┌──────────────┐      1       1     ┌──────────────────┐
│     User     │ ────────────────► │   RefreshToken   │
│──────────────│                    │──────────────────│
│ id           │                    │ id               │
│ username     │                    │ token            │
│ email        │                    │ user (FK)        │
│ password     │                    │ expiryDate       │
│ role (RoleName)│                  │ revoked          │
│ status (UserStatus)│              └──────────────────┘
└──────────────┘
      │ 1
      │
      ▼
┌──────────────────┐      ┌──────────────────────┐
│  RevokedToken    │      │  PasswordResetToken  │
│──────────────────│      │──────────────────────│
│ id               │      │ id                   │
│ token            │      │ token                │
│ user (FK)        │      │ user (FK)            │
│ revokedAt        │      │ expiryDate           │
└──────────────────┘      └──────────────────────┘
```

### 4.4 API Endpoints

**AuthenticationController** — `/api/v1/auth`

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/auth/login` | Authenticate and issue tokens | Public |
| POST | `/api/v1/auth/logout` | Revoke refresh token | Authenticated |
| POST | `/api/v1/auth/refresh` | Issue new access token | Public (valid refresh) |
| POST | `/api/v1/auth/change-password` | Change own password | Authenticated |
| POST | `/api/v1/auth/forgot-password` | Request password reset email | Public |
| POST | `/api/v1/auth/reset-password` | Reset password with token | Public |
| GET | `/api/v1/auth/me` | Get current user profile | Authenticated |

**AdminController** — admin operations

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/admin/register` | Register admin user | SUPER_ADMIN |
| POST | `/api/v1/admin/grant-role` | Grant role to user | SUPER_ADMIN |
| PATCH | `/api/v1/admin/user-status` | Update user status | SUPER_ADMIN |

### 4.5 Key Design Patterns

- **Token-based authentication** with access + refresh token rotation
- **Token revocation** for logout and security enforcement
- **Email-based password reset** flow with expiring tokens
- **Mapper pattern** for entity ↔ DTO conversion

### 4.6 Dependencies

- **Security** module (JWT generation/validation)
- **Common** module (`BaseEntity`, exceptions, `ApiResponse`)
- **Employee** module (user ↔ employee linkage)

### 4.7 Security / Access Control

- Login, refresh, forgot/reset password are **public** endpoints
- All other operations require a valid JWT
- Admin operations restricted to `SUPER_ADMIN`

---

## 5. Module 2 — Security

**Package**: `com.my_hourly.security`

### 5.1 Overview & Purpose

Provides the security infrastructure: JWT token management, authentication filter chain, user details loading, and security configuration. It is the enforcement point for all access control.

### 5.2 Package Structure

```
com.my_hourly.security
├── config/
│   ├── SecurityConfig.java
│   ├── SecurityConstants.java
│   ├── PasswordConfig.java
│   └── WebConfig.java
├── handler/
│   ├── JwtAuthenticationEntryPoint.java
│   └── JwtAccessDeniedHandler.java
├── token/
│   ├── JwtService.java
│   ├── JwtServiceImpl.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtProperties.java
│   └── TokenType.java
├── user/
│   ├── CustomUserDetails.java
│   └── CustomUserDetailsService.java
└── util/
    └── SecurityUtils.java
```

### 5.3 Security Configuration

- **Stateless** JWT-based session management
- **CORS** configured via `WebConfig`
- **CSRF** disabled (stateless API)
- **Public endpoints** defined in `SecurityConstants`
- **Password encoding** via `PasswordConfig` (BCrypt)

### 5.4 Authentication Flow

```
Request
  │
  ▼
JwtAuthenticationFilter
  │  extracts Bearer token
  ▼
JwtService (validate signature, expiry, type)
  │
  ▼
CustomUserDetailsService (load user, assign authorities by role)
  │
  ▼
SecurityContext (authenticated principal)
  │
  ▼
Controller (method-level @PreAuthorize)
```

### 5.5 Key Design Patterns

- **Filter chain** pattern (`JwtAuthenticationFilter` in the security filter chain)
- **Strategy pattern** (`JwtService` interface with `JwtServiceImpl`)
- **Adapter pattern** (`CustomUserDetails` adapts `User` to Spring Security principal)
- **Factory/Configuration** via `@Configuration` beans

### 5.6 Dependencies

- **Authentication** module (User entity, RoleName)
- **Common** module (exceptions, constants)

### 5.7 Security / Access Control

- Enforces JWT validation on all protected endpoints
- Role-based authorities derived from `RoleName`
- Centralized entry point and access-denied handlers return standardized error responses

---

## 6. Module 3 — Master Data

**Package**: `com.my_hourly.master`

### 6.1 Overview & Purpose

Manages organizational master data: departments, designations, and job titles. These form the organizational hierarchy used across employee, payroll, and reporting modules.

### 6.2 Package Structure

```
com.my_hourly.master
├── api/
│   ├── controller/
│   │   ├── DepartmentController.java
│   │   ├── DesignationController.java
│   │   └── JobTitleController.java
│   ├── request/    # Create/Update Department, Designation, JobTitle
│   └── response/   # DepartmentResponse, DesignationResponse, JobTitleResponse
├── entity/
│   ├── Department.java
│   ├── Designation.java
│   └── JobTitle.java
├── mapper/
│   ├── DepartmentMapper.java
│   ├── DesignationMapper.java
│   └── JobTitleMapper.java
├── repository/
│   ├── DepartmentRepository.java
│   ├── DesignationRepository.java
│   └── JobTitleRepository.java
└── service/
    ├── DepartmentService.java
    ├── DesignationService.java
    ├── JobTitleService.java
    └── impl/
        ├── DepartmentServiceImpl.java
        ├── DesignationServiceImpl.java
        └── JobTitleServiceImpl.java
```

### 6.3 Entity Relationship Diagram

```
┌────────────────┐
│   Department   │
│────────────────│
│ id             │
│ code           │
│ name           │
│ description    │
│ active         │
└───────┬────────┘
        │ 1
        │ has many
        ▼
┌────────────────┐
│   Designation  │
│────────────────│
│ id             │
│ code           │
│ name           │
│ department (FK)│
│ description    │
│ active         │
└───────┬────────┘
        │ 1
        │ has many
        ▼
┌────────────────┐
│    JobTitle    │
│────────────────│
│ id             │
│ code           │
│ title          │
│ designation(FK)│
│ active         │
└────────────────┘
```

**Hierarchy**: `Department → Designation → JobTitle`

### 6.4 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET/POST | `/api/v1/departments` | List / create departments |
| GET/PUT/DELETE | `/api/v1/departments/{id}` | Read / update / delete department |
| GET/POST | `/api/v1/designations` | List / create designations |
| GET/PUT/DELETE | `/api/v1/designations/{id}` | Read / update / delete designation |
| GET/POST | `/api/v1/job-titles` | List / create job titles |
| GET/PUT/DELETE | `/api/v1/job-titles/{id}` | Read / update / delete job title |

### 6.5 Key Design Patterns

- **Service + Impl** separation (interface + implementation)
- **Mapper pattern** for DTO conversion
- **Soft-delete / active flag** for master data lifecycle

### 6.6 Dependencies

- **Common** module (`BaseEntity`, exceptions)
- Referenced by **Employee**, **Payroll**, **Report** modules

### 6.7 Security / Access Control

- Read operations available to authenticated users
- Write operations restricted to `SUPER_ADMIN` and `HR_ADMIN`

---

## 7. Module 4 — Employee

**Package**: `com.my_hourly.employee`

### 7.1 Overview & Purpose

Manages the employee master record, including personal details, organizational assignment, reporting hierarchy, profile photo, and search capabilities.

### 7.2 Package Structure

```
com.my_hourly.employee
├── api/
│   ├── controller/EmployeeController.java
│   ├── request/    # CreateEmployeeRequest, UpdateEmployeeRequest,
│   │               # UpdateEmployeeByEmployeeRequest
│   └── response/   # EmployeeResponse, EmployeeDropdownResponse
├── entity/
│   ├── Employee.java
│   ├── EmploymentType.java
│   └── Gender.java
├── mapper/EmployeeMapper.java
├── repository/EmployeeRepository.java
├── service/
│   ├── EmployeeService.java
│   └── impl/EmployeeServiceImpl.java
├── specification/EmployeeSpecification.java
└── validator/MultipartFileValidator.java
```

### 7.3 Entity Relationship Diagram

```
┌──────────────────┐
│     Employee     │
│──────────────────│
│ id               │
│ employeeCode     │
│ firstName        │
│ lastName         │
│ email            │
│ phone            │
│ gender (Gender)  │
│ dob              │
│ doj              │
│ employmentType   │
│ profilePhoto     │
│ roleName         │
│ active           │
└────┬─────┬───────┘
     │     │
     │     └──────────► Department (FK)
     │                 Designation (FK)
     │                 JobTitle (FK)
     │                 User (FK)
     │
     └────────────────► reportingManager (self-FK → Employee)
```

### 7.4 API Endpoints

**EmployeeController** — `/api/v1/employees`

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/employees` | List / search employees (specification) | Authenticated |
| POST | `/api/v1/employees` | Create employee | HR_ADMIN, SUPER_ADMIN |
| GET | `/api/v1/employees/{id}` | Get employee by id | Authenticated |
| PUT | `/api/v1/employees/{id}` | Update employee | HR_ADMIN, SUPER_ADMIN |
| DELETE | `/api/v1/employees/{id}` | Delete employee | HR_ADMIN, SUPER_ADMIN |
| POST | `/api/v1/employees/{id}/photo` | Upload profile photo | Owner, HR_ADMIN |
| GET | `/api/v1/employees/dropdown` | Employee dropdown list | Authenticated |
| GET | `/api/v1/employees/search` | Advanced search | Authenticated |

### 7.5 Key Design Patterns

- **Specification pattern** (`EmployeeSpecification`) for dynamic search
- **Self-referencing relationship** for reporting manager hierarchy
- **Validator** (`MultipartFileValidator`) for file upload validation
- **Mapper pattern** for DTO conversion

### 7.6 Dependencies

- **Master** module (Department, Designation, JobTitle)
- **Authentication** module (User, RoleName)
- **Common** module (BaseEntity, FileStorageService)

### 7.7 Security / Access Control

- Read/search available to all authenticated users
- Create/update/delete restricted to `HR_ADMIN` and `SUPER_ADMIN`
- Profile photo upload restricted to the employee themselves or HR

---

## 8. Module 5 — Attendance

**Package**: `com.my_hourly.attendance`

### 8.1 Overview & Purpose

Tracks employee daily attendance including check-in/check-out, breaks, geo-location, overtime, and provides monthly summaries, dashboards, and calendar views.

### 8.2 Package Structure

```
com.my_hourly.attendance
├── api/
│   ├── controller/AttendanceController.java
│   ├── request/    # CheckInRequest, CheckOutRequest, BreakStartRequest
│   └── response/   # CheckInResponse, CheckOutResponse, BreakStartResponse,
│                   # BreakEndResponse, AttendanceResponse,
│                   # AttendanceMonthlySummaryResponse, AttendanceDashboardResponse,
│                   # AttendanceCalendarResponse
├── entity/
│   ├── Attendance.java
│   ├── AttendanceBreak.java
│   ├── AttendanceStatus.java
│   ├── BreakType.java
│   └── EmployeeStatus.java
├── mapper/AttendanceMapper.java
├── repository/
│   ├── AttendanceRepository.java
│   └── AttendanceBreakRepository.java
├── service/
│   ├── AttendanceService.java
│   ├── AttendanceValidationService.java
│   └── impl/
│       ├── AttendanceServiceImpl.java
│       └── AttendanceValidationServiceImpl.java
├── specification/AttendanceSpecification.java
├── scheduler/AttendanceScheduler.java
└── util/
    ├── DateTimeUtil.java
    └── TimeUtil.java
```

### 8.3 Entity Relationship Diagram

```
┌──────────────────┐
│    Attendance    │
│──────────────────│
│ id               │
│ employee (FK)    │
│ attendanceDate   │
│ checkInTime      │
│ checkOutTime     │
│ workingMinutes   │
│ totalBreakMinutes│
│ attendanceStatus │
│ employeeStatus   │
│ geoLatitude      │
│ geoLongitude     │
│ lateMinutes      │
│ earlyExitMinutes │
│ overtimeMinutes  │
└───────┬──────────┘
        │ 1
        │ has many
        ▼
┌──────────────────┐
│  AttendanceBreak │
│──────────────────│
│ id               │
│ attendance (FK)  │
│ breakType        │
│ startTime        │
│ endTime          │
│ durationMinutes  │
└──────────────────┘
```

### 8.4 API Endpoints

**AttendanceController** — `/api/v1/attendance`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/attendance/check-in` | Record check-in |
| POST | `/api/v1/attendance/check-out` | Record check-out |
| POST | `/api/v1/attendance/break/start` | Start a break |
| POST | `/api/v1/attendance/break/end` | End a break |
| GET | `/api/v1/attendance/date` | Get attendance by date |
| GET | `/api/v1/attendance/month` | Get attendance by month |
| GET | `/api/v1/attendance/monthly-summary` | Monthly summary |
| GET | `/api/v1/attendance/dashboard` | Dashboard data |
| GET | `/api/v1/attendance/calendar` | Calendar view |

### 8.5 Key Design Patterns

- **Validation service** separation (`AttendanceValidationService`) for business rule checks
- **Scheduler** (`AttendanceScheduler`) for automatic check-out
- **Specification pattern** for querying
- **Utility classes** (`DateTimeUtil`, `TimeUtil`) for time calculations

### 8.6 Dependencies

- **Employee** module (employee FK)
- **Common** module (BaseEntity, exceptions)
- **Settings** module (attendance rules)

### 8.7 Security / Access Control

- Employees manage their own attendance
- Managers/HR can view team attendance
- Scheduler runs with system privileges

---

## 9. Module 6 — Leave

**Package**: `com.my_hourly.leave`

### 9.1 Overview & Purpose

Manages the complete leave lifecycle: leave types, balances, requests, approvals, allocations, and transactions, including expiry handling and multi-level approval workflows.

### 9.2 Package Structure

```
com.my_hourly.leave
├── api/
│   ├── controller/
│   │   ├── LeaveRequestController.java
│   │   ├── LeaveTypeController.java
│   │   ├── LeaveBalanceController.java
│   │   ├── LeaveApprovalController.java
│   │   ├── LeaveAllocationController.java
│   │   └── LeaveTransactionController.java
│   ├── request/    # LeaveRequestRequest, LeaveTypeRequest, LeaveActionRequest
│   └── response/   # LeaveRequestResponse, LeaveTypeResponse, LeaveBalanceResponse,
│                   # LeaveApprovalResponse, LeaveTransactionResponse
├── entity/
│   ├── LeaveRequest.java
│   ├── LeaveType.java
│   ├── LeaveBalance.java
│   ├── LeaveApproval.java
│   ├── LeaveTransaction.java
│   ├── LeaveStatus.java
│   ├── LeaveAction.java
│   ├── LeaveAllocationType.java
│   ├── LeaveTransactionType.java
│   ├── ApprovalLevel.java
│   └── MonthType.java
├── mapper/
│   ├── LeaveRequestMapper.java
│   ├── LeaveTypeMapper.java
│   ├── LeaveBalanceMapper.java
│   ├── LeaveApprovalMapper.java
│   └── LeaveTransactionMapper.java
├── repository/
│   ├── LeaveRequestRepository.java
│   ├── LeaveTypeRepository.java
│   ├── LeaveBalanceRepository.java
│   ├── LeaveApprovalRepository.java
│   └── LeaveTransactionRepository.java
├── service/
│   ├── LeaveRequestService.java
│   ├── LeaveTypeService.java
│   ├── LeaveBalanceService.java
│   ├── LeaveApprovalService.java
│   ├── LeaveAllocationService.java
│   ├── LeaveTransactionService.java
│   ├── LeaveValidationService.java
│   ├── LeaveAuthorizationService.java
│   ├── LeaveExpiryService.java
│   └── impl/ (corresponding Impl classes)
├── specification/LeaveSpecification.java
├── scheduler/LeaveScheduler.java
└── context/LeaveApplicationContext.java
```

### 9.3 Entity Relationship Diagram

```
┌──────────────────┐
│    LeaveType     │
│──────────────────│
│ id               │
│ name             │
│ code             │
│ allocationType   │
│ maxDays          │
└──────┬───────────┘
       │ 1
       │
       ▼
┌──────────────────┐      ┌──────────────────┐
│   LeaveRequest   │      │   LeaveBalance   │
│──────────────────│      │──────────────────│
│ id               │      │ id               │
│ employee (FK)    │      │ employee (FK)    │
│ leaveType (FK)   │      │ leaveType (FK)   │
│ startDate        │      │ allocatedDays    │
│ endDate          │      │ usedDays         │
│ totalDays        │      │ remainingDays    │
│ reason           │      └──────────────────┘
│ status           │
└──────┬───────────┘
       │ 1
       │ has many
       ▼
┌──────────────────┐      ┌──────────────────┐
│  LeaveApproval   │      │ LeaveTransaction │
│──────────────────│      │──────────────────│
│ id               │      │ id               │
│ leaveRequest (FK)│      │ leaveBalance (FK)│
│ approver (FK)    │      │ transactionType  │
│ approvalLevel    │      │ days             │
│ action           │      │ date             │
│ comment          │      └──────────────────┘
└──────────────────┘
```

### 9.4 API Endpoints

| Controller | Base Path | Key Operations |
|------------|-----------|----------------|
| LeaveRequestController | `/api/v1/leaves` | Create, list, update, cancel leave requests |
| LeaveTypeController | `/api/v1/leave-types` | CRUD leave types |
| LeaveBalanceController | `/api/v1/leave-balances` | View balances |
| LeaveApprovalController | `/api/v1/leave-approvals` | Approve / reject requests |
| LeaveAllocationController | `/api/v1/leave-allocations` | Allocate leave |
| LeaveTransactionController | `/api/v1/leave-transactions` | View transactions |

### 9.5 Key Design Patterns

- **State machine** for leave status transitions (PENDING → APPROVED/REJECTED)
- **Multi-level approval** workflow (`ApprovalLevel`)
- **Strategy/Context** pattern (`LeaveApplicationContext`)
- **Validation service** for business rules
- **Authorization service** for permission checks
- **Scheduler** (`LeaveScheduler`) for balance expiry

### 9.6 Dependencies

- **Employee** module (employee FK)
- **Master** module (organizational data)
- **Common** module (BaseEntity, exceptions)
- **Settings** module (leave policies)

### 9.7 Security / Access Control

- Employees create and view their own requests
- Managers/HR approve requests based on approval level
- HR manages leave types, allocations, and balances

---

## 10. Module 7 — Holiday

**Package**: `com.my_hourly.holiday`

### 10.1 Overview & Purpose

Manages company holidays, including recurring holidays and whether attendance is allowed on a holiday.

### 10.2 Package Structure

```
com.my_hourly.holiday
├── api/
│   ├── controller/HolidayController.java
│   ├── request/    # CreateHolidayRequest, UpdateHolidayRequest
│   └── response/   # HolidayResponse, HolidayCalendarResponse
├── entity/
│   ├── Holiday.java
│   └── HolidayType.java
├── mapper/HolidayMapper.java
├── repository/HolidayRepository.java
├── service/
│   ├── HolidayService.java
│   └── impl/HolidayServiceImpl.java
└── specification/HolidaySpecification.java
```

### 10.3 Entity Fields

| Field | Type | Description |
|-------|------|-------------|
| `holidayDate` | LocalDate | Date of the holiday |
| `holidayName` | String | Name of the holiday |
| `holidayType` | HolidayType | Type (e.g., NATIONAL, FESTIVAL) |
| `description` | String | Optional description |
| `attendanceAllowed` | boolean | Whether attendance is allowed |
| `active` | boolean | Active flag |
| `recurring` | boolean | Whether it recurs yearly |

### 10.4 API Endpoints

**HolidayController** — `/api/v1/holidays`

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/holidays` | List holidays (specification) | Authenticated |
| POST | `/api/v1/holidays` | Create holiday | HR_ADMIN, SUPER_ADMIN |
| GET | `/api/v1/holidays/{id}` | Get holiday | Authenticated |
| PUT | `/api/v1/holidays/{id}` | Update holiday | HR_ADMIN, SUPER_ADMIN |
| DELETE | `/api/v1/holidays/{id}` | Delete holiday | HR_ADMIN, SUPER_ADMIN |
| GET | `/api/v1/holidays/calendar` | Holiday calendar | Authenticated |

### 10.5 Key Design Patterns

- **Specification pattern** for filtering
- **Mapper pattern** for DTO conversion

### 10.6 Dependencies

- **Common** module (BaseEntity, exceptions)
- Consumed by **Calendar** and **Attendance** modules

### 10.7 Security / Access Control

- Read available to all authenticated users
- Write restricted to `HR_ADMIN` and `SUPER_ADMIN`

---

## 11. Module 8 — Payroll

**Package**: `com.my_hourly.payroll`

### 11.1 Overview & Purpose

Handles payroll processing: salary structures, templates, payroll generation, approval workflow, payment, history, and payslip PDF generation.

### 11.2 Package Structure

```
com.my_hourly.payroll
├── api/
│   ├── controller/
│   │   ├── PayrollController.java
│   │   ├── SalaryStructureController.java
│   │   ├── SalaryTemplateController.java
│   │   └── EmployeePaymentDetailsController.java
│   └── dto/
│       ├── request/   # CreatePayrollRequest, UpdateDraftPayrollRequest,
│       │              # MarkPayrollPaidRequest, CreateSalaryStructureRequest,
│       │              # CreateSalaryTemplateRequest, UpdateSalaryTemplateRequest,
│       │              # CreateEmployeePaymentDetailsRequest,
│       │              # UpdateEmployeePaymentDetailsRequest, CreateSalaryRevisionRequest
│       └── response/  # PayrollResponse, PayrollSummaryResponse, PayrollHistoryResponse,
│                      # SalaryStructureResponse, SalaryTemplateResponse, FailedPayroll
├── entity/
│   ├── Payroll.java
│   ├── PayrollHistory.java
│   ├── SalaryStructure.java
│   ├── SalaryTemplate.java
│   └── EmployeePaymentDetails.java
├── enums/
│   ├── PayrollStatus.java
│   ├── SalaryComponentType.java
│   ├── SalaryStructureStatus.java
│   ├── PaymentMode.java
│   ├── PayrollAction.java
│   └── PayrollHistoryAction.java
├── pdf/
│   ├── PayslipGenerator.java
│   ├── PayslipPdfService.java
│   └── PayslipPdfServiceImpl.java
├── repository/
│   ├── PayrollRepository.java
│   ├── PayrollHistoryRepository.java
│   ├── SalaryStructureRepository.java
│   ├── SalaryTemplateRepository.java
│   └── EmployeePaymentDetailsRepository.java
└── service/
    ├── PayrollService.java
    ├── PayrollHistoryService.java
    ├── SalaryStructureService.java
    ├── SalaryTemplateService.java
    ├── EmployeePaymentDetailsService.java
    └── impl/ (corresponding Impl classes)
```

### 11.3 Entity Relationship Diagram

```
┌──────────────────┐
│  SalaryTemplate  │
│──────────────────│
│ id               │
│ name             │
│ earnings (JSON)  │
│ deductions (JSON)│
└──────┬───────────┘
       │ 1
       │
       ▼
┌──────────────────┐
│ SalaryStructure  │
│──────────────────│
│ id               │
│ employee (FK)    │
│ template (FK)    │
│ effectiveFrom    │
│ effectiveTo      │
│ earnings (JSON)  │
│ deductions (JSON)│
│ grossPay         │
│ netPay           │
│ status           │
└──────┬───────────┘
       │ 1
       │
       ▼
┌──────────────────┐      ┌──────────────────┐
│     Payroll      │      │  PayrollHistory  │
│──────────────────│      │──────────────────│
│ id               │      │ id               │
│ payrollNumber    │      │ payroll (FK)     │
│ version          │      │ action           │
│ active           │      │ changedBy        │
│ employee (FK)    │      │ changedAt        │
│ salaryStructure  │      │ details          │
│ payrollMonth     │      └──────────────────┘
│ earnings (JSON)  │
│ deductions (JSON)│
│ netPay           │
│ status           │
│ paymentDate      │
│ paymentReference │
└──────────────────┘

┌──────────────────┐
│EmployeePaymentDetails│
│──────────────────│
│ id               │
│ employee (FK)    │
│ bankName         │
│ accountNumber    │
│ ifscCode         │
│ paymentMode      │
└──────────────────┘
```

### 11.4 Payroll Status Lifecycle

```
DRAFT ──► GENERATED ──► APPROVED ──► PAID
   │          │            │
   └──────────┴────────────┴──► SUPERSEDED / CANCELLED
```

### 11.5 API Endpoints

| Controller | Base Path | Key Operations |
|------------|-----------|----------------|
| PayrollController | `/api/v1/payroll` | Create, generate, approve, mark paid, list, summary, payslip |
| SalaryStructureController | `/api/v1/salary-structures` | CRUD salary structures, revisions |
| SalaryTemplateController | `/api/v1/salary-templates` | CRUD salary templates |
| EmployeePaymentDetailsController | `/api/v1/payment-details` | CRUD employee payment details |

### 11.6 Key Design Patterns

- **State machine** for payroll status transitions
- **Versioning** for payroll revisions
- **Snapshot pattern** (employee snapshot fields stored on payroll)
- **History/audit** via `PayrollHistory`
- **Strategy pattern** for PDF generation (`PayslipPdfService`)

### 11.7 Dependencies

- **Employee** module (employee FK)
- **Master** module (organizational data)
- **Common** module (BaseEntity, exceptions, file storage)
- **Settings** module (payroll/company settings)

### 11.8 Security / Access Control

- Payroll processing restricted to `PAYROLL_ADMIN` and `SUPER_ADMIN`
- Employees can view their own payslips
- Approval workflow enforces separation of duties

---

## 12. Module 9 — Performance

**Package**: `com.my_hourly.performance`

### 12.1 Overview & Purpose

Manages employee performance reviews, including monthly and yearly reviews, ratings, scores, and manager feedback.

### 12.2 Package Structure

```
com.my_hourly.performance
├── api/
│   ├── controller/PerformanceController.java
│   ├── request/    # CreatePerformanceReviewRequest, UpdatePerformanceReviewRequest,
│   │               # PerformanceReviewFilterRequest
│   └── response/   # PerformanceReviewResponse, PerformanceSummaryResponse
├── entity/
│   ├── PerformanceReview.java
│   ├── PerformanceRating.java
│   ├── ReviewStatus.java
│   └── ReviewType.java
├── mapper/PerformanceReviewMapper.java
├── repository/PerformanceReviewRepository.java
├── service/
│   ├── PerformanceService.java
│   └── impl/PerformanceServiceImpl.java
└── specification/PerformanceReviewSpecification.java
```

### 12.3 Entity Fields

| Field | Type | Description |
|-------|------|-------------|
| `employee` | FK | Employee under review |
| `reviewer` | FK | Reviewing manager |
| `reviewType` | ReviewType | MONTHLY / YEARLY |
| `reviewMonth` | int | Month of review |
| `reviewYear` | int | Year of review |
| `rating` | PerformanceRating | EXCELLENT … NEEDS_IMPROVEMENT |
| `score` | double | Numeric score |
| `strengths` | String | Strengths text |
| `improvements` | String | Improvement areas |
| `managerFeedback` | String | Manager feedback |
| `employeeComment` | String | Employee comment |
| `reviewDate` | LocalDate | Review date |
| `status` | ReviewStatus | DRAFT / COMPLETED |

### 12.4 API Endpoints

**PerformanceController** — `/api/v1/performance`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/performance` | List / search reviews (filters) |
| POST | `/api/v1/performance` | Create review |
| GET | `/api/v1/performance/{id}` | Get review |
| PUT | `/api/v1/performance/{id}` | Update review |
| DELETE | `/api/v1/performance/{id}` | Delete review |
| POST | `/api/v1/performance/{id}/complete` | Complete review |
| GET | `/api/v1/performance/summary` | Performance summary |

### 12.5 Key Design Patterns

- **Specification pattern** for filtered search
- **State transition** (DRAFT → COMPLETED)
- **Mapper pattern** for DTO conversion

### 12.6 Dependencies

- **Employee** module (employee/reviewer FK)
- **Common** module (BaseEntity, exceptions)

### 12.7 Security / Access Control

- Managers create/complete reviews for their team
- Employees view their own reviews
- HR/Admin manage all reviews

---

## 13. Module 10 — Report

**Package**: `com.my_hourly.report`

### 13.1 Overview & Purpose

Generates attendance and leave reports with multiple export formats (JSON, Excel, PDF) and summary statistics.

### 13.2 Package Structure

```
com.my_hourly.report
├── api/controller/ReportController.java
├── dto/
│   ├── request/    # AttendanceReportRequest, LeaveReportRequest
│   └── response/   # AttendanceReportResponse, AttendanceReportPageResponse,
│                   # AttendanceSummaryResponse, LeaveReportResponse,
│                   # LeaveReportPageResponse, LeaveSummaryResponse
├── export/
│   ├── AttendanceExcelExporter.java
│   ├── AttendancePdfExporter.java
│   ├── LeaveExcelExporter.java
│   └── LeavePdfExporter.java
├── service/
│   ├── AttendanceReportService.java
│   ├── LeaveReportService.java
│   └── impl/
│       ├── AttendanceReportServiceImpl.java
│       └── LeaveReportServiceImpl.java
└── specification/
    ├── AttendanceReportSpecification.java
    └── LeaveReportSpecification.java
```

### 13.3 API Endpoints

**ReportController** — `/api/v1/reports`

| Method | Endpoint | Description | Formats |
|--------|----------|-------------|---------|
| GET | `/api/v1/reports/attendance` | Attendance report | JSON, Excel, PDF |
| GET | `/api/v1/reports/attendance/summary` | Attendance summary | JSON |
| GET | `/api/v1/reports/leave` | Leave report | JSON, Excel, PDF |
| GET | `/api/v1/reports/leave/summary` | Leave summary | JSON |

### 13.4 Key Design Patterns

- **Strategy pattern** for export formats (Excel/PDF exporters)
- **Specification pattern** for report filtering
- **Pagination** via `PageResponse`

### 13.5 Dependencies

- **Attendance** module (data source)
- **Leave** module (data source)
- **Employee** module (employee data)
- **Common** module (PageResponse, exceptions)

### 13.6 Security / Access Control

- Reports restricted to `HR_ADMIN`, `MANAGER`, `SUPER_ADMIN`, `PAYROLL_ADMIN`
- Managers limited to their team scope

---

## 14. Module 11 — Settings

**Package**: `com.my_hourly.settings`

### 14.1 Overview & Purpose

Provides unified configuration management for attendance, leave, company, notification, and work-log settings through a single controller.

### 14.2 Package Structure

```
com.my_hourly.settings
├── BaseSettings.java
├── controller/SettingController.java
├── attendance/   # entity, repository, service(+impl), mapper, dto
├── leave/        # entity, repository, service(+impl), mapper, dto
├── company/      # entity, repository, service(+impl), mapper, dto
├── notification/ # entity, repository, service(+impl), mapper, dto
└── workLogs/     # entity, repository, service(+impl), mapper, dto
```

### 14.3 Sub-modules

| Sub-module | Entity | Purpose |
|------------|--------|---------|
| AttendanceSettings | `AttendanceSettings` | Attendance rules (work hours, grace, overtime) |
| LeaveSettings | `LeaveSettings` | Leave policies |
| CompanySettings | `CompanySettings` | Company profile |
| NotificationSettings | `NotificationSettings` | Notification preferences |
| WorkLogSettings | `WorkLogSettings` | Work log rules |

### 14.4 API Endpoints

**SettingController** — `/api/v1/settings`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/settings/{type}` | Get settings by type |
| PUT | `/api/v1/settings/{type}` | Update settings by type |

### 14.5 Key Design Patterns

- **Template method / inheritance** via `BaseSettings`
- **Unified controller** dispatching to sub-module services
- **Mapper pattern** per sub-module

### 14.6 Dependencies

- **Common** module (BaseEntity, exceptions)
- Consumed by Attendance, Leave, Notification, Payroll modules

### 14.7 Security / Access Control

- Restricted to `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER`

---

## 15. Module 12 — Calendar

**Package**: `com.my_hourly.calendar`

### 15.1 Overview & Purpose

Aggregates calendar events (holidays, birthdays, work anniversaries, leaves, attendance) into a unified calendar view.

### 15.2 Package Structure

```
com.my_hourly.calendar
├── api/controller/CalendarController.java
├── dto/response/CalendarResponse.java, CalendarEventResponse.java
├── enums/CalendarEventType.java, CalendarView.java
└── service/
    ├── CalendarService.java
    └── impl/CalendarServiceImpl.java
```

### 15.3 Enums

| Enum | Values |
|------|--------|
| `CalendarEventType` | HOLIDAY, BIRTHDAY, WORK_ANNIVERSARY, LEAVE, ATTENDANCE |
| `CalendarView` | PERSONAL, ORGANIZATION |

### 15.4 API Endpoints

**CalendarController** — `/api/v1/calendar`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/calendar` | Get events by month/year/view/eventTypes |

### 15.5 Key Design Patterns

- **Facade pattern** — aggregates data from multiple modules (Holiday, Employee, Leave, Attendance, Notification)
- **Filtering** by event type and view

### 15.6 Dependencies

- **Holiday**, **Employee**, **Leave**, **Attendance**, **Notification** modules

### 15.7 Security / Access Control

- PERSONAL view limited to the authenticated employee
- ORGANIZATION view available to all authenticated users

---

## 16. Module 13 — Notification

**Package**: `com.my_hourly.notification`

### 16.1 Overview & Purpose

Manages in-app notifications and announcements, with automated schedulers for birthdays, holidays, and work anniversaries.

### 16.2 Package Structure

```
com.my_hourly.notification
├── api/
│   ├── controller/NotificationController.java
│   ├── request/AnnouncementRequest.java
│   └── response/NotificationResponse.java
├── entity/
│   ├── Notification.java
│   ├── Announcement.java
│   ├── NotificationType.java
│   ├── NotificationPriority.java
│   └── ReferenceType.java
├── mapper/NotificationMapper.java
├── repository/
│   ├── NotificationRepository.java
│   └── AnnouncementRepository.java
├── service/
│   ├── NotificationService.java
│   └── impl/NotificationServiceImpl.java
└── scheduler/
    ├── BirthdayScheduler.java
    ├── HolidayScheduler.java
    ├── WorkAnniversaryScheduler.java
    └── NotificationScheduler.java
```

### 16.3 Entity Fields

| Field | Type | Description |
|-------|------|-------------|
| `employee` | FK | Target employee |
| `title` | String | Notification title |
| `message` | String | Notification body |
| `notificationType` | NotificationType | Type |
| `priority` | NotificationPriority | Priority level |
| `referenceType` | ReferenceType | Linked entity type |
| `referenceId` | Long | Linked entity id |
| `isRead` | boolean | Read flag |

### 16.4 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/notifications` | List my notifications |
| PATCH | `/api/v1/notifications/{id}/read` | Mark as read |
| POST | `/api/v1/announcements` | Create announcement (HR) |

### 16.5 Key Design Patterns

- **Scheduler pattern** for automated notifications
- **Observer-like** generation of notifications from domain events
- **Mapper pattern** for DTO conversion

### 16.6 Dependencies

- **Employee** module (employee FK)
- **Holiday** module (holiday notifications)
- **Common** module (BaseEntity, exceptions)
- **Settings** module (notification preferences)

### 16.7 Security / Access Control

- Employees view their own notifications
- Announcements created by `HR_ADMIN` / `SUPER_ADMIN`

---

## 17. Module 14 — Project (Stub)

**Package**: `com.my_hourly.project`

### 17.1 Status

> ⚠️ **STUB / PLACEHOLDER** — Not yet implemented.

### 17.2 Planned Structure

```
com.my_hourly.project
├── controller/
│   ├── ProjectController.java
│   └── ClientController.java
└── entity/
    ├── Project.java
    ├── Client.java
    ├── EmployeeAllocation.java
    └── ProjectMember.java
```

### 17.3 Planned Entities

| Entity | Purpose |
|--------|---------|
| `Project` | Project master data |
| `Client` | Client master data |
| `EmployeeAllocation` | Employee-to-project allocation |
| `ProjectMember` | Project membership |

### 17.4 Notes

- Controllers and entities are empty skeletons
- No services, repositories, or DTOs implemented yet
- Future integration with Timesheet module expected

---

## 18. Module 15 — Timesheet (Stub)

**Package**: `com.my_hourly.timesheet`

### 18.1 Status

> ⚠️ **STUB / PLACEHOLDER** — Not yet implemented.

### 18.2 Planned Structure

```
com.my_hourly.timesheet
├── controller/TimesheetController.java
└── entity/
    ├── Timesheet.java
    ├── TimesheetEntry.java
    └── TimesheetApproval.java
```

### 18.3 Planned Entities

| Entity | Purpose |
|--------|---------|
| `Timesheet` | Weekly/bi-weekly timesheet |
| `TimesheetEntry` | Daily time entries |
| `TimesheetApproval` | Approval workflow |

### 18.4 Notes

- Controller and entities are empty skeletons
- No services, repositories, or DTOs implemented yet
- Future integration with Project and Attendance modules expected

---

## 19. Module 16 — Common

**Package**: `com.my_hourly.common`

### 19.1 Overview & Purpose

Provides shared infrastructure used across all modules: base entity, configuration, constants, exceptions, API payloads, file storage, and Swagger setup.

### 19.2 Package Structure

```
com.my_hourly.common
├── entity/BaseEntity.java
├── config/
│   ├── JacksonConfig.java
│   ├── R2Config.java
│   ├── StringToEnumConverterFactory.java
│   └── CloudinaryConfig.java
├── constants/AppConstants.java
├── enums/
│   ├── EmployeeStatus.java
│   └── ErrorCode.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── BusinessException.java
│   ├── DuplicateResourceException.java
│   ├── ForbiddenException.java
│   ├── UnauthorizedException.java
│   ├── ValidationException.java
│   └── ErrorResponseFactory.java
├── payload/
│   ├── ApiResponse.java
│   ├── ApiError.java
│   └── PageResponse.java
├── service/
│   ├── FileStorageService.java
│   ├── FileStorageServiceCloudFlare.java
│   ├── CloudinaryFileStorageServiceImpl.java
│   └── R2FileStorageServiceImpl.java
├── swagger/SwaggerConfig.java
└── initializer/DataInitializer.java
```

### 19.3 Key Components

| Component | Purpose |
|-----------|---------|
| `BaseEntity` | Base for all entities (id, createdAt, updatedAt) |
| `GlobalExceptionHandler` | Centralized exception → error response mapping |
| `ApiResponse` / `ApiError` / `PageResponse` | Standard response envelopes |
| `FileStorageService` | Abstraction for file storage (Cloudinary, R2) |
| `SwaggerConfig` | OpenAPI documentation configuration |
| `DataInitializer` | Application startup data initialization |
| `StringToEnumConverterFactory` | String → enum conversion for query params |

### 19.4 Key Design Patterns

- **Strategy pattern** for file storage backends
- **Factory pattern** (`ErrorResponseFactory`)
- **Global exception handling** (AOP-style `@ControllerAdvice`)
- **Converter factory** for enum binding

### 19.5 Dependencies

- **None** (foundational module, depended upon by all others)

---

## 20. Module 17 — Seed

**Package**: `com.my_hourly.seed`

### 20.1 Overview & Purpose

Seeds initial reference and demo data into the database from CSV files at application startup.

### 20.2 Package Structure

```
com.my_hourly.seed
├── config/
│   ├── SeedRunner.java
│   └── CsvReader.java
├── master/
│   ├── DepartmentSeeder.java
│   ├── DesignationSeeder.java
│   └── JobTitleSeeder.java
├── employee/
│   ├── EmployeeSeeder.java
│   └── UserSeeder.java
├── attendance/AttendanceSeeder.java
├── holiday/HolidaySeeder.java
├── LeaveTypeSeeder/LeaveTypeSeeder.java
├── LeaveBalanceSeeder/LeaveBalanceSeeder.java
└── LeaveRequestSeeder/LeaveRequestSeeder.java
```

### 20.3 Seeders

| Seeder | Data Seeded |
|--------|-------------|
| DepartmentSeeder | Departments |
| DesignationSeeder | Designations |
| JobTitleSeeder | Job titles |
| EmployeeSeeder | Employees |
| UserSeeder | Users |
| AttendanceSeeder | Attendance records |
| HolidaySeeder | Holidays |
| LeaveTypeSeeder | Leave types |
| LeaveBalanceSeeder | Leave balances |
| LeaveRequestSeeder | Leave requests |

### 20.4 Data Sources

- CSV files located in `resources/seed/`
- Read via `CsvReader` configuration

### 20.5 Key Design Patterns

- **Runner pattern** (`SeedRunner` implements `ApplicationRunner`)
- **CSV import** via `CsvReader`

### 20.6 Dependencies

- All domain modules (Master, Employee, Attendance, Holiday, Leave)
- **Common** module (BaseEntity)

### 20.7 Security / Access Control

- Runs at application startup with system context
- Typically disabled in production via configuration

---

## 21. Module 18 — Audit

**Package**: `com.my_hourly.audit`

### 21.1 Status

> ℹ️ **No dedicated files currently exist** in this package.

### 21.2 Notes

- Audit capabilities are currently handled implicitly via:
  - `BaseEntity` timestamps (`createdAt`, `updatedAt`)
  - `PayrollHistory` for payroll changes
  - `LeaveApproval` / `LeaveTransaction` for leave actions
- A dedicated audit trail module may be introduced in the future for comprehensive change tracking.

---

## 22. Module 19 — Lookup

**Package**: `com.my_hourly.lookup`

### 22.1 Status

> ℹ️ **No dedicated files currently exist** in this package.

### 22.2 Notes

- Lookup/reference data is currently managed within their respective domain modules (e.g., Master module for departments/designations/job titles, Leave module for leave types).
- A centralized lookup module may be introduced in the future.

---

## 23. Module 20 — Util

**Package**: `com.my_hourly.util`

### 23.1 Overview & Purpose

Provides cross-cutting utility classes used across modules.

### 23.2 Package Structure

```
com.my_hourly.util
├── SecurityUtils.java
├── StringUtils.java
└── ValidationUtils.java
```

### 23.3 Utilities

| Utility | Purpose |
|---------|---------|
| `SecurityUtils` | Security context helpers (current user, roles) |
| `StringUtils` | String manipulation helpers |
| `ValidationUtils` | Common validation helpers |

### 23.4 Dependencies

- **Common** module (exceptions for validation errors)

---

## 24. Module Dependency Graph

```
                        ┌──────────────┐
                        │    Common    │  (foundation)
                        └──────┬───────┘
                               │
        ┌──────────┬───────────┼───────────┬──────────┬──────────┐
        ▼          ▼           ▼           ▼          ▼          ▼
   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
   │Security│ │ Master │ │  Util  │ │  Seed  │ │Settings│ │  Auth  │
   └───┬────┘ └───┬────┘ └────────┘ └───┬────┘ └───┬────┘ └───┬────┘
       │          │                     │          │          │
       │          ▼                     │          │          ▼
       │     ┌────────┐                 │          │     ┌────────┐
       │     │Employee│◄────────────────┼──────────┼─────┤        │
       │     └───┬────┘                 │          │     └────────┘
       │         │                      │          │
       ▼         ▼                      ▼          ▼
   ┌────────┐ ┌────────┐          ┌────────┐  ┌────────┐
   │  Auth  │ │Attend. │          │ Holiday│  │ Leave  │
   └────────┘ └───┬────┘          └───┬────┘  └───┬────┘
                  │                   │           │
                  ▼                   ▼           ▼
             ┌────────┐          ┌────────┐  ┌────────┐
             │ Report │          │Calendar│  │ Notif. │
             └────────┘          └────────┘  └────────┘
                  │
                  ▼
             ┌────────┐
             │ Payroll│
             └────────┘
```

### Dependency Summary

| Module | Depends On |
|--------|-----------|
| Common | — (foundation) |
| Security | Common, Authentication |
| Authentication | Security, Common, Employee |
| Master | Common |
| Employee | Master, Authentication, Common |
| Attendance | Employee, Common, Settings |
| Leave | Employee, Master, Common, Settings |
| Holiday | Common |
| Payroll | Employee, Master, Common, Settings |
| Performance | Employee, Common |
| Report | Attendance, Leave, Employee, Common |
| Settings | Common |
| Calendar | Holiday, Employee, Leave, Attendance, Notification |
| Notification | Employee, Holiday, Common, Settings |
| Project (stub) | — |
| Timesheet (stub) | — |
| Seed | All domain modules, Common |
| Util | Common |

---

## 25. Security & Access Control Matrix

| Module | Read | Write | Admin |
|--------|------|-------|-------|
| Authentication | Authenticated | Owner / SUPER_ADMIN | SUPER_ADMIN |
| Security | — (infrastructure) | — | — |
| Master | Authenticated | HR_ADMIN, SUPER_ADMIN | SUPER_ADMIN |
| Employee | Authenticated | HR_ADMIN, SUPER_ADMIN | SUPER_ADMIN |
| Attendance | Owner / Manager | Owner | HR_ADMIN |
| Leave | Owner / Manager | Owner / HR | HR_ADMIN |
| Holiday | Authenticated | HR_ADMIN, SUPER_ADMIN | SUPER_ADMIN |
| Payroll | Owner (payslip) | PAYROLL_ADMIN | SUPER_ADMIN |
| Performance | Owner / Manager | Manager | HR_ADMIN |
| Report | HR, Manager, Payroll, Super | — | SUPER_ADMIN |
| Settings | SUPER_ADMIN, HR_ADMIN, MANAGER | Same | SUPER_ADMIN |
| Calendar | Authenticated | — | — |
| Notification | Owner | HR_ADMIN | SUPER_ADMIN |
| Project (stub) | — | — | — |
| Timesheet (stub) | — | — | — |
| Seed | — (startup) | — | — |

---

## 26. Design Patterns Summary

| Pattern | Where Used |
|---------|------------|
| **Layered Architecture** | All modules (Controller → Service → Repository → Entity) |
| **DTO Pattern** | All modules (request/response objects) |
| **Mapper Pattern** | All modules (entity ↔ DTO conversion) |
| **Specification Pattern** | Employee, Attendance, Leave, Holiday, Performance, Report |
| **Strategy Pattern** | JWT service, File storage, Report exporters, PDF generation |
| **State Machine** | Leave status, Payroll status, Performance review status |
| **Facade Pattern** | Calendar (aggregates multiple modules) |
| **Scheduler Pattern** | Attendance, Leave, Notification schedulers |
| **Template Method / Inheritance** | Settings (`BaseSettings`) |
| **Filter Chain** | Security (`JwtAuthenticationFilter`) |
| **Adapter Pattern** | Security (`CustomUserDetails`) |
| **Factory Pattern** | Common (`ErrorResponseFactory`) |
| **Global Exception Handling** | Common (`GlobalExceptionHandler`) |
| **Runner Pattern** | Seed (`SeedRunner`) |
| **Snapshot Pattern** | Payroll (employee snapshot fields) |
| **Versioning** | Payroll (version field, history) |

---

*End of Document — MyHourly HRMS Module Design Documentation*
