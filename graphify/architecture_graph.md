# MyHourly — Architecture Graph

> Spring Boot 4.0 · Java 21 · JPA · JWT · MySQL/PostgreSQL

**Open the interactive graph:** [myhourly_architecture.html](file:///C:/Users/User/.gemini/antigravity-ide/brain/7f6897ce-5411-4497-b906-17e6e56578ab/myhourly_architecture.html)

The graph is a fully interactive, force-directed canvas visualization with:
- 🖱️ **Drag nodes** to rearrange layout
- 🔍 **Click a node** to see the info panel (file counts, entities, endpoints, deps)
- 🎯 **Click a module in the sidebar** to highlight its connections
- 🖱️ **Scroll** to zoom, **drag background** to pan
- **Reset View** button to restore initial layout

---

## Module Dependency Graph

```mermaid
graph TD
  subgraph Core["🔧 Core Infrastructure"]
    COMMON["common\n(BaseEntity, ApiResponse,\nGlobalExceptionHandler)"]
    SECURITY["security\n(JWT, Spring Security,\nWebConfig)"]
    UTIL["util\n(SecurityUtils, StringUtils,\nValidationUtils)"]
    AUDIT["audit\n(AuditLog)"]
  end

  subgraph Identity["🔐 Identity & Access"]
    AUTH["authentication\n(User, RefreshToken,\nRoleName)"]
    EMP["employee\n(Employee, EmploymentType,\nGender)"]
  end

  subgraph MasterData["📦 Master Data"]
    MASTER["master\n(Department, Designation,\nJobTitle)"]
    LOOKUP["lookup\n(LookupController)"]
    SETTINGS["settings\n(Attendance, Company,\nLeave, Notification, WorkLog)"]
  end

  subgraph WorkTracking["⏱️ Work Tracking"]
    ATT["attendance\n(Attendance, AttendanceBreak,\nBreakType, EmployeeStatus)"]
    TIME["timesheet\n(Timesheet, TimesheetEntry,\nTimesheetApproval)"]
    CAL["calendar\n(CalendarEventType,\nCalendarView)"]
  end

  subgraph LeaveSystem["🌴 Leave System"]
    LEAVE["leave\n(LeaveRequest, LeaveBalance,\nLeaveType, LeaveApproval,\nLeaveTransaction)"]
    HOL["holiday\n(Holiday)"]
  end

  subgraph BusinessModules["💼 Business Modules"]
    PAYROLL["payroll\n(Payroll, Salary)"]
    PROJ["project\n(Project, Client)"]
    PERF["performance\n(PerformanceController)"]
    NOTIF["notification\n(Notification, scheduler)"]
  end

  subgraph DataInit["🌱 Data Initialisation"]
    SEED["seed\n(SeedRunner, CsvReader,\n8 seeders)"]
  end

  %% Core deps
  AUTH --> COMMON
  AUTH --> SECURITY
  SECURITY --> AUTH
  UTIL --> COMMON
  AUDIT --> COMMON

  %% Identity deps
  EMP --> AUTH
  EMP --> COMMON
  EMP --> MASTER

  %% Master deps
  MASTER --> COMMON
  LOOKUP --> COMMON
  LOOKUP --> MASTER
  LOOKUP --> EMP
  SETTINGS --> COMMON

  %% Work tracking deps
  ATT --> EMP
  ATT --> LEAVE
  ATT --> SETTINGS
  ATT --> COMMON
  TIME --> EMP
  TIME --> PROJ
  TIME --> COMMON
  CAL --> ATT
  CAL --> LEAVE
  CAL --> HOL
  CAL --> EMP

  %% Leave deps
  LEAVE --> EMP
  LEAVE --> ATT
  LEAVE --> NOTIF
  LEAVE --> SETTINGS
  LEAVE --> COMMON
  HOL --> COMMON

  %% Business deps
  PAYROLL --> EMP
  PAYROLL --> COMMON
  PROJ --> EMP
  PROJ --> COMMON
  PERF --> EMP
  PERF --> COMMON
  NOTIF --> EMP
  NOTIF --> COMMON

  %% Seed deps
  SEED --> EMP
  SEED --> LEAVE
  SEED --> MASTER
  SEED --> HOL
  SEED --> ATT

  style COMMON  fill:#a78bfa,color:#000,stroke:#7c3aed
  style SECURITY fill:#f43f5e,color:#000,stroke:#be123c
  style AUTH    fill:#6366f1,color:#fff,stroke:#4338ca
  style EMP     fill:#22d3ee,color:#000,stroke:#0891b2
  style ATT     fill:#f59e0b,color:#000,stroke:#d97706
  style LEAVE   fill:#10b981,color:#000,stroke:#059669
  style PAYROLL fill:#ec4899,color:#000,stroke:#db2777
  style TIME    fill:#8b5cf6,color:#fff,stroke:#7c3aed
  style NOTIF   fill:#f97316,color:#000,stroke:#ea580c
  style PROJ    fill:#06b6d4,color:#000,stroke:#0891b2
  style PERF    fill:#a3e635,color:#000,stroke:#65a30d
  style CAL     fill:#fb7185,color:#000,stroke:#e11d48
  style HOL     fill:#fbbf24,color:#000,stroke:#d97706
  style MASTER  fill:#94a3b8,color:#000,stroke:#64748b
  style SETTINGS fill:#7dd3fc,color:#000,stroke:#0284c7
  style AUDIT   fill:#34d399,color:#000,stroke:#059669
  style LOOKUP  fill:#818cf8,color:#fff,stroke:#4f46e5
  style SEED    fill:#d1d5db,color:#000,stroke:#9ca3af
  style UTIL    fill:#9ca3af,color:#000,stroke:#6b7280
```

---

## Module Layer Breakdown

| Module | Controllers | Services | Repositories | Entities | Total Files |
|--------|:-----------:|:--------:|:------------:|:--------:|:-----------:|
| 🔐 Authentication | 2 | 2 | 3 | 5 | ~14 |
| 👤 Employee | 1 | 2 | 1 | 3 | ~10 |
| 🕐 Attendance | 1 | 2 | 2 | 5 | ~18 |
| 🌴 Leave | 5 | 5 | 4 | 5 | ~28 |
| 💰 Payroll | 3 | — | — | 2 | ~5 |
| 📋 Timesheet | 1 | — | — | 3 | ~4 |
| 🔔 Notification | 1 | 1 | 1 | 1 | ~7 |
| 🏗️ Project | 2 | — | — | — | ~2 |
| 📈 Performance | 1 | — | — | — | ~1 |
| 📅 Calendar | 1 | 2 | — | — | ~5 |
| 🎉 Holiday | 1 | 2 | 1 | 1 | ~6 |
| 📦 Master Data | 3 | 3 | 3 | 3 | ~12 |
| ⚙️ Settings | 1 | 4 | 4 | 4 | ~22 |
| 🛡️ Security | — | — | — | — | ~11 |
| 🔧 Common | — | — | — | 1 | ~16 |
| 📝 Audit | — | 1 | — | 1 | ~2 |
| 🔍 Lookup | 1 | — | — | — | ~1 |
| 🌱 Seed | — | — | — | — | ~11 |
| 🛠️ Util | — | — | — | — | ~3 |

---

## Key Architectural Observations

> [!NOTE]
> **Layered Architecture** — Every domain module follows a strict `Controller → Service → Repository → Entity` pattern. DTOs are cleanly separated into `api/request` and `api/response` packages.

> [!TIP]
> **Central Hub: `common` module** — `BaseEntity`, `ApiResponse<T>`, `PageResponse<T>`, `GlobalExceptionHandler`, and `ErrorCode` are shared by all 19 modules. This is the architectural foundation.

> [!IMPORTANT]
> **Most Complex Module: `leave`** — 5 controllers, 5 services, 4 repositories, scheduler, and cross-cutting integration with `attendance`, `notification`, `settings`, and `employee`. It is the primary business-critical domain.

> [!NOTE]
> **Security Model** — JWT-based stateless authentication with refresh tokens, revoked token tracking, and `CustomUserDetails`. Spring Security's `SecurityFilterChain` is configured in `security.config`.

> [!TIP]
> **Incomplete Modules** — `payroll`, `timesheet`, `project`, and `performance` currently only have controllers/entities scaffolded. Services and repositories are not yet implemented — good candidates for next development sprints.

> [!NOTE]
> **Settings Architecture** — The `settings` module uses a `BaseSettings` abstract class with 5 domain-specific sub-modules (attendance, company, leave, notification, workLogs), each with their own entity/repo/service/mapper/controller.
