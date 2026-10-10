# Graph Report - my_hourly  (2026-10-10)

## Corpus Check
- 555 files · ~212,007 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 26 file(s) not represented in the graph (top: .csv 9, (none) 6, .properties 6)

## Summary
- 4371 nodes · 13662 edges · 216 communities (87 shown, 129 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 901 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `30474c46`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- io.swagger.v3.oas.annotations.Operation
- lombok.RequiredArgsConstructor
- Form16
- Employee
- com.my_hourly.employee.entity.Employee
- LeaveRequest
- LeaveRequestManagerActionTest.java
- lombok
- ErrorCode
- NotificationResponse
- AttendanceService
- Form16QuarterResponse
- ApiResponse
- LeaveApproval
- Form16ChapterVIAResponse
- Form16Section16DeductionResponse
- Form16ChallanResponse
- Form16ChapterVIAServiceImpl
- lombok.Getter
- Form16EmployerMasterResponse
- EmployeeRepository
- ResourceNotFoundException
- Form16VerificationResponse
- com.my_hourly.leave.api.response.LeaveRequestResponse
- PayslipGenerator
- LeaveExpiryServiceTest
- Attendance
- Form16LastFieldsResponse
- JobTitle
- org.springframework.security.access.prepost.PreAuthorize
- DepartmentResponse
- DesignationResponse
- LeaveRequestMonthFilterTest.java
- PayslipGenerator.java
- CalendarServiceImpl
- LookupResponse
- CompanySettings
- AuthenticationController
- PayrollServiceImpl.java
- AttendanceRegularizationServiceTest.java
- org.junit.jupiter.api.DisplayName
- AdminController
- Leave PAID / LOP Split & Approver — Frontend Implementation Guide
- notnull
- PayslipGeneratorOld
- AttendanceResponse
- EmployeeServiceImpl.java
- Form16Response
- Form16Section16DeductionServiceImpl.java
- LeaveRequestMonthAllocation.java
- AttendanceSettings
- org.springframework.data.jpa.repository.JpaRepository
- Form16ExemptionResponse
- JobTitleController
- AttendanceReportServiceImpl.java
- Design Review: Configurable Sandwich Leave Policy (Re-review #2)
- Technical Design: Configurable Sandwich Leave Policy
- SlipSample
- GlobalExceptionHandler.java
- PayrollResponse
- org.springframework.stereotype.Component
- CheckInRequest
- PayrollServiceImpl
- PerformanceReview
- AttendanceStatus
- EmployeePaymentDetailsResponse
- Form16SalaryRequest
- org.springframework.data.jpa.repository.Query
- LeaveMonthlyUsageIntegrationTest.java
- JwtServiceImpl.java
- User
- Form16LastFieldsServiceImpl
- JwtAccessDeniedHandler.java
- Form16Verification
- LeaveBalance
- org.springframework.stereotype.Service
- Payroll
- PerformanceReviewResponse
- Sandwich Leave Policy - Implementation Verification
- EmploymentType
- DesignationController
- Form16EmployerMasterServiceImpl
- SalaryStructureResponse
- .applyLeave
- org.springframework.context.annotation.Configuration
- ValidationException
- LeaveSettings
- Graphify Setup and Usage Guide
- AttendanceMonthlySummaryResponse
- LeaveType
- CreatePayrollRequest
- PerformanceServiceImpl.java
- LeaveTypeResponse
- Notification
- SalaryStructure
- JwtAuthenticationFilter.java
- CalendarController.java
- LookupServiceImpl.java
- Holiday
- LeaveBalanceResponse
- SalaryStructureServiceImpl
- Backend implementation of configurable Sandwich Leave policy
- LeaveRequestQueryStatusTest.java
- Form16Salary
- CheckInResponse
- Form16ExemptionServiceImpl
- LeaveTransaction
- DepartmentController
- org.springframework.transaction.annotation.Transactional
- EmployeePaymentDetails
- MultipartFileValidator.java
- MarkPayrollPaidRequest
- SecurityConfig.java
- org.springframework.data.jpa.domain.Specification
- LeaveTransactionResponse
- MonthType
- SalaryTemplate
- PayrollHistory
- AttendanceRegularizationDetail
- HolidayResponse
- LeaveTypeServiceImpl
- LeaveApplicationContext
- WebConfig.java
- PasswordResetToken
- LeaveEmailService.java
- BreakEndResponse
- RevokedToken
- org.springframework.http.ResponseEntity
- Form16Exemption
- AttendanceServiceImpl
- LeaveTransactionController
- LeaveAllocationServiceImpl
- FailedPayroll
- Form16ServiceImpl
- LeaveValidationServiceImpl
- CreatePerformanceReviewRequest
- AttendanceBreak
- CheckOutRequest
- CheckOutResponse
- LeaveAllocationService
- Form16EmployerMaster
- ioexception
- SalaryTemplateResponse
- PayrollAction
- CreateSalaryRevisionRequest
- FinancialYearUtil
- EmployeeStatus
- UpdateSalaryTemplateRequest
- apply-leave-approvals-edits.mjs
- graphify
- SalaryComponentType
- TokenType
- fix-integration-test.mjs
- AppConstants
- LeaveAllocationType
- SecurityConstants
- AGENTS.md
- AuditEntity.java
- my_hourly/util/SecurityUtils.java
- StringUtils.java
- ValidationUtils.java
- Form16ChapterVIA
- Form16SalaryServiceImpl
- com.my_hourly:my_hourly
- Form16LastFields
- Form16Section16Deduction
- MyHourlyApplication.java
- 6. Screen-by-screen implementation plan
- CreateSalaryTemplateRequest
- RegeneratePayrollRequest
- UpdateDraftPayrollRequest
- AttendanceSummary

## God Nodes (most connected - your core abstractions)
1. `Employee` - 177 edges
2. `ResourceNotFoundException` - 131 edges
3. `ErrorCode` - 120 edges
4. `ApiResponse` - 118 edges
5. `LeaveRequest` - 98 edges
6. `Attendance` - 73 edges
7. `AttendanceStatus` - 63 edges
8. `ValidationException` - 62 edges
9. `LeaveRequestRepository` - 57 edges
10. `Payroll` - 57 edges

## Surprising Connections (you probably didn't know these)
- `Test coverage comprehensive with edge cases` --references--> `LeaveSandwichPolicyTest`  [INFERRED]
  .agents/tasks/sandwich-leave/2025-01-22-033000-code-review.md → src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
- `Verification Summary` --references--> `LeaveSandwichPolicyTest`  [INFERRED]
  .agents/tasks/sandwich-leave/plan.md → src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
- `1. Original Dates Preserved` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/verification.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `PAID/LOP Split (Not in Worktree)` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/verification.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `9. Error handling` --references--> `ApiError`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/common/payload/response/ApiError.java

## Import Cycles
- None detected.

## Communities (216 total, 129 thin omitted)

### Community 0 - "io.swagger.v3.oas.annotations.Operation"
Cohesion: 0.07
Nodes (4): EmployeePaymentDetailsController, SalaryStructureController, SalaryTemplateController, PerformanceController

### Community 1 - "lombok.RequiredArgsConstructor"
Cohesion: 0.08
Nodes (19): B2FileStorageServiceImpl, LeaveEmailService, LeaveTypeRepository, Department, Designation, DepartmentRepository, DesignationRepository, JobTitleRepository (+11 more)

### Community 2 - "Form16"
Cohesion: 0.15
Nodes (3): Form16, Form16Repository, Form16CertificateNumberGenerator

### Community 4 - "com.my_hourly.employee.entity.Employee"
Cohesion: 0.06
Nodes (15): Attendance Overlap Validation, Existing Leave Balance Logic, Integration Points, PAID/LOP Split (Not in Worktree), 12. Known backend gaps & open questions, 1.1 Approved days are split into PAID and LOP, 1.2 The monthly allowance decides how many days can be PAID, 1.3 The approver is recorded on the request (+7 more)

### Community 5 - "LeaveRequest"
Cohesion: 0.04
Nodes (10): 2. API surface, LeaveRequestResponse, LeaveRequest, TypeReference<Set<LocalDate>>, LeaveRequestMapper, LeaveRequestRepository, LeaveAuthorizationServiceImpl, LeaveAuthorizationService (+2 more)

### Community 6 - "LeaveRequestManagerActionTest.java"
Cohesion: 0.07
Nodes (7): MonthAllocation, PaidLopAllocation, LeaveRequestServiceImpl, LeavePaidLopService, LeaveSpecification, MonthRange, LeaveRequestManagerActionTest

### Community 8 - "ErrorCode"
Cohesion: 0.03
Nodes (63): ErrorCode, ACCESS_DENIED, APPROVAL_PENDING, ATTENDANCE_ALREADY_EXISTS, ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE, ATTENDANCE_DATE_OUT_OF_RANGE, ATTENDANCE_NOT_FOUND, BAD_REQUEST (+55 more)

### Community 9 - "NotificationResponse"
Cohesion: 0.13
Nodes (3): NotificationResponse, NotificationController, AnnouncementService

### Community 11 - "Form16QuarterResponse"
Cohesion: 0.06
Nodes (7): Form16QuarterController, Form16QuarterRequest, Form16QuarterResponse, Form16Quarter, Form16QuarterRepository, Form16QuarterService, Form16QuarterServiceImpl

### Community 12 - "ApiResponse"
Cohesion: 0.06
Nodes (6): AttendanceRegularizationController, ApiResponse, EmployeeController, LeaveBalanceController, LookupController, SettingController

### Community 13 - "LeaveApproval"
Cohesion: 0.07
Nodes (10): LeaveActionRequest, LeaveApprovalResponse, LeaveApproval, ApprovalLevel, HR, MANAGER, LeaveAction, APPROVE (+2 more)

### Community 14 - "Form16ChapterVIAResponse"
Cohesion: 0.10
Nodes (4): Form16ChapterVIAController, Form16ChapterVIARequest, Form16ChapterVIAResponse, Form16ChapterVIAService

### Community 15 - "Form16Section16DeductionResponse"
Cohesion: 0.10
Nodes (4): Form16Section16DeductionController, Form16Section16DeductionRequest, Form16Section16DeductionResponse, Form16Section16DeductionService

### Community 16 - "Form16ChallanResponse"
Cohesion: 0.07
Nodes (7): Form16ChallanListResponse, Form16ChallanRequest, Form16ChallanResponse, Form16Challan, Form16ChallanRepository, Form16ChallanService, Form16ChallanServiceImpl

### Community 18 - "lombok.Getter"
Cohesion: 0.10
Nodes (22): 6.5 Reports — **backend gap, no frontend change yet**, BreakEndRequest, AdminRegisterRequest, EmployeeRegisterRequest, GrantRoleRequest, UpdateUserStatusRequest, RefreshTokenResponse, RegisterResponse (+14 more)

### Community 19 - "Form16EmployerMasterResponse"
Cohesion: 0.09
Nodes (4): Form16EmployerMasterController, Form16EmployerMasterRequest, Form16EmployerMasterResponse, Form16EmployerMasterService

### Community 20 - "EmployeeRepository"
Cohesion: 0.06
Nodes (36): EmployeeRepository, HolidayRepository, LeaveScheduler, NotificationPriority, HIGH, LOW, MEDIUM, NotificationType (+28 more)

### Community 22 - "Form16VerificationResponse"
Cohesion: 0.10
Nodes (4): Form16VerificationController, Form16VerificationRequest, Form16VerificationResponse, Form16VerificationService

### Community 23 - "com.my_hourly.leave.api.response.LeaveRequestResponse"
Cohesion: 0.11
Nodes (4): LeaveApprovalController, LeaveRequestController, LeaveTypeController, LeaveRequestService

### Community 25 - "LeaveExpiryServiceTest"
Cohesion: 0.11
Nodes (7): ExpiryEntry, LeaveExpiryPlan, MappedPaidDaysProjection, PaidDaysProjection, LeaveExpiryServiceImpl, LeaveExpiryService, LeaveExpiryServiceTest

### Community 26 - "Attendance"
Cohesion: 0.11
Nodes (4): Attendance, AttendanceRepository, AttendanceValidationService, AttendanceValidationServiceImpl

### Community 27 - "Form16LastFieldsResponse"
Cohesion: 0.10
Nodes (4): Form16LastFieldsController, Form16LastFieldsRequest, Form16LastFieldsResponse, Form16LastFieldsService

### Community 28 - "JobTitle"
Cohesion: 0.07
Nodes (7): CreateJobTitleRequest, UpdateJobTitleRequest, JobTitleResponse, JobTitle, JobTitleMapper, JobTitleServiceImpl, JobTitleService

### Community 30 - "DepartmentResponse"
Cohesion: 0.08
Nodes (6): CreateDepartmentRequest, UpdateDepartmentRequest, DepartmentResponse, DepartmentMapper, DepartmentService, DepartmentServiceImpl

### Community 31 - "DesignationResponse"
Cohesion: 0.08
Nodes (6): CreateDesignationRequest, UpdateDesignationRequest, DesignationResponse, DesignationMapper, DesignationService, DesignationServiceImpl

### Community 34 - "CalendarServiceImpl"
Cohesion: 0.08
Nodes (13): CalendarEventResponse, CalendarResponse, CalendarEventType, ATTENDANCE, BIRTHDAY, HOLIDAY, LEAVE, WORK_ANNIVERSARY (+5 more)

### Community 35 - "LookupResponse"
Cohesion: 0.08
Nodes (4): EmployeeIdName, LookupResponse, ReportingManagerLookupResponse, LookupService

### Community 36 - "CompanySettings"
Cohesion: 0.09
Nodes (7): CompanySettingsRequest, CompanySettingsResponse, CompanySettings, CompanySettingsMapper, CompanySettingsRepository, CompanySettingsService, CompanySettingsServiceImpl

### Community 37 - "AuthenticationController"
Cohesion: 0.06
Nodes (6): AuthenticationController, ChangePasswordRequest, ForgotPasswordRequest, RefreshTokenRequest, ResetPasswordRequest, AuthenticationService

### Community 38 - "PayrollServiceImpl.java"
Cohesion: 0.11
Nodes (11): PayrollHistoryResponse, PayrollHistoryAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED, SUPERSEDED (+3 more)

### Community 39 - "AttendanceRegularizationServiceTest.java"
Cohesion: 0.12
Nodes (11): CreateRegularizationDetailRequest, CreateRegularizationRequest, RegularizationDetailActionRequest, RegularizationDetailResponse, RegularizationResponse, RegularizationDetailStatus, APPROVED, PENDING (+3 more)

### Community 40 - "org.junit.jupiter.api.DisplayName"
Cohesion: 0.07
Nodes (5): RegularizationValidator, AttendanceRegularizationServiceTest, LeavePaidLopAllocationTest, LeaveRequestMonthFilterTest, LeaveSandwichPolicyTest

### Community 42 - "Leave PAID / LOP Split & Approver — Frontend Implementation Guide"
Cohesion: 0.07
Nodes (30): Algorithm Details, Risk 2: Existing leave requests with pending approval, Sandwich Leave Expansion Logic, 1. Basics, 2. Endpoints you need, 4.1 Working days and sandwich leave, 4.2 PAID vs LOP (important), 4.3 Submission rules (`POST`) (+22 more)

### Community 43 - "notnull"
Cohesion: 0.09
Nodes (9): AnnouncementRequest, UploadType, MAGAZINE, POST, PaymentMode, BANK_TRANSFER, CASH, CHEQUE (+1 more)

### Community 46 - "EmployeeServiceImpl.java"
Cohesion: 0.13
Nodes (10): CreateEmployeeRequest, UpdateEmployeeByEmployeeRequest, UpdateEmployeeRequest, EmployeeDropdownResponse, EmployeeResponse, Gender, FEMALE, MALE (+2 more)

### Community 50 - "AttendanceSettings"
Cohesion: 0.11
Nodes (5): AttendanceSettingsRequest, AttendanceSettings, AttendanceSettingsMapper, AttendanceSettingsService, AttendanceSettingsServiceImpl

### Community 51 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.15
Nodes (4): Form16ExemptionRepository, Form16LastFieldsRepository, Form16SalaryRepository, LeaveRequestMonthAllocationRepository

### Community 52 - "Form16ExemptionResponse"
Cohesion: 0.10
Nodes (4): Form16ExemptionController, Form16ExemptionRequest, Form16ExemptionResponse, Form16ExemptionService

### Community 54 - "AttendanceReportServiceImpl.java"
Cohesion: 0.06
Nodes (17): LeaveStatus, APPROVED, CANCELLED, PENDING, REJECTED, ReportController, AttendanceReportRequest, LeaveReportRequest (+9 more)

### Community 55 - "Design Review: Configurable Sandwich Leave Policy (Re-review #2)"
Cohesion: 0.07
Nodes (29): 1. No-Double-Counting Algorithm, 2. Integration Completeness, 3. DB Migration Safety, 4. API Contract Backward Compatibility, 5. Test Coverage, 6. Were Previous Findings Addressed?, Algorithm Verification, Attendance Overlap Check Verification (+21 more)

### Community 56 - "Technical Design: Configurable Sandwich Leave Policy"
Cohesion: 0.10
Nodes (19): 1. Database Schema, 2. Backend - Entity Layer, 3. Backend - DTO Layer, 4. Backend - Mapper Layer, 6. Frontend - Settings UI, Affected Components, Database Migration, HIGH Severity Findings - All Resolved (+11 more)

### Community 58 - "GlobalExceptionHandler.java"
Cohesion: 0.18
Nodes (3): ErrorResponseFactory, GlobalExceptionHandler, ApiError

### Community 60 - "org.springframework.stereotype.Component"
Cohesion: 0.09
Nodes (10): AttendanceRegularizationMapper, AttendanceScheduler, DataInitializer, LeaveApprovalMapper, LeaveApprovalServiceImpl, SeedRunner, AttendanceSettingsRepository, LeaveSettingsMapper (+2 more)

### Community 63 - "PerformanceReview"
Cohesion: 0.15
Nodes (5): PerformanceReview, ReviewType, MONTHLY, YEARLY, PerformanceReviewRepository

### Community 64 - "AttendanceStatus"
Cohesion: 0.06
Nodes (22): BreakStartRequest, AttendanceDashboardResponse, AttendanceStatus, ABSENT, HALF_DAY, HOLIDAY, LATE, LEAVE (+14 more)

### Community 65 - "EmployeePaymentDetailsResponse"
Cohesion: 0.12
Nodes (4): CreateEmployeePaymentDetailsRequest, EmployeePaymentDetailsResponse, EmployeePaymentDetailsService, EmployeePaymentDetailsServiceImpl

### Community 66 - "Form16SalaryRequest"
Cohesion: 0.14
Nodes (3): Form16SalaryController, Form16SalaryRequest, Form16SalaryService

### Community 67 - "org.springframework.data.jpa.repository.Query"
Cohesion: 0.15
Nodes (3): RevokedTokenRepository, LeaveApprovalRepository, NotificationRepository

### Community 70 - "User"
Cohesion: 0.03
Nodes (25): LoginRequest, LoginResponse, UserProfileResponse, RefreshToken, RoleName, CLIENT, EMPLOYEE, HR_ADMIN (+17 more)

### Community 73 - "Form16Verification"
Cohesion: 0.19
Nodes (3): Form16Verification, Form16VerificationRepository, Form16VerificationServiceImpl

### Community 75 - "org.springframework.stereotype.Service"
Cohesion: 0.09
Nodes (6): BadRequestException, DuplicateResourceException, NotificationMapper, AnnouncementRepository, AnnouncementServiceImpl, PayslipPdfServiceImpl

### Community 76 - "Payroll"
Cohesion: 0.09
Nodes (9): Payroll, PayrollStatus, APPROVED, CANCELLED, DRAFT, GENERATED, PAID, SUPERSEDED (+1 more)

### Community 77 - "PerformanceReviewResponse"
Cohesion: 0.20
Nodes (3): PerformanceReviewResponse, PerformanceServiceImpl, PerformanceService

### Community 78 - "Sandwich Leave Policy - Implementation Verification"
Cohesion: 0.07
Nodes (27): 1. Database Migration, 1. Original Dates Preserved, 2. Entity Layer, 2. Forced Working Days Storage, 3. DTO Layer, 3. Rule Precedence, 4. Backward Compatibility, 4. Mapper Layer (+19 more)

### Community 79 - "EmploymentType"
Cohesion: 0.33
Nodes (5): EmploymentType, CONTRACT, FULL_TIME, INTERN, PART_TIME

### Community 82 - "SalaryStructureResponse"
Cohesion: 0.15
Nodes (3): CreateSalaryStructureRequest, SalaryStructureResponse, SalaryStructureService

### Community 83 - ".applyLeave"
Cohesion: 0.22
Nodes (7): Risk 1: Double-counting in edge cases, Risk 3: Integration with LeavePaidLopServiceImpl, Risk 4: UI confusion - displayed date range vs selected date range, Risk 5: Performance impact on leave validation, Risk 6: Database migration rollback, Risk 7: Attendance marking for sandwich weekends, Risk Assessment and Mitigation

### Community 84 - "org.springframework.context.annotation.Configuration"
Cohesion: 0.09
Nodes (6): JpaAuditConfig, JacksonConfig, SchedulingConfig, B2StorageConfig, SwaggerConfig, PasswordConfig

### Community 85 - "ValidationException"
Cohesion: 0.10
Nodes (10): AttendanceRegularization, RegularizationStatus, APPROVED, CANCELLED, PARTIALLY_APPROVED, PENDING, REJECTED, AttendanceRegularizationRepository (+2 more)

### Community 86 - "LeaveSettings"
Cohesion: 0.09
Nodes (4): LeaveSettingsRequest, LeaveSettingsResponse, LeaveSettings, LeaveSettingsService

### Community 87 - "Graphify Setup and Usage Guide"
Cohesion: 0.11
Nodes (17): 1. Project location, 2. Graphify version and executable, 3. Refresh the graph after code changes, 4. Commands at a glance, 5. Git hooks and when they run, 6. Codebuff MCP configuration, 7. Recommended workflow for AI-assisted code changes, 8. Troubleshooting (+9 more)

### Community 89 - "LeaveType"
Cohesion: 0.14
Nodes (3): LeaveTypeRequest, LeaveType, LeaveTypeMapper

### Community 91 - "PerformanceServiceImpl.java"
Cohesion: 0.11
Nodes (10): PerformanceReviewFilterRequest, PerformanceRating, AVERAGE, EXCELLENT, GOOD, NEEDS_IMPROVEMENT, VERY_GOOD, ReviewStatus (+2 more)

### Community 94 - "SalaryStructure"
Cohesion: 0.16
Nodes (5): SalaryStructure, SalaryStructureStatus, ACTIVE, INACTIVE, SalaryStructureRepository

### Community 95 - "JwtAuthenticationFilter.java"
Cohesion: 0.07
Nodes (5): JwtAuthenticationFilter, JwtService, CustomUserDetails, CustomUserDetailsService, SecurityUtils

### Community 98 - "Holiday"
Cohesion: 0.14
Nodes (3): Holiday, HolidayServiceImpl, HolidaySpecification

### Community 99 - "LeaveBalanceResponse"
Cohesion: 0.13
Nodes (8): 3. Payloads, Apply — `POST /leave-requests`, Leave settings (`GET/PUT /settings/leave`), `LeaveBalanceResponse`, `LeaveRequestResponse` (the main object), Manager action — `PUT /leave-approvals/{id}/leave-approval-by-manager`, LeaveBalanceResponse, LeaveBalanceMapper

### Community 101 - "Backend implementation of configurable Sandwich Leave policy"
Cohesion: 0.20
Nodes (9): Backend implementation of configurable Sandwich Leave policy, DTO fields marked @NotNull in LeaveSettingsRequest break backward compatibility, Empty file artifact `Mon-only)` in commit, Forced working days persisted as JSON in LeaveRequest entity, Frontend Settings UI missing from commit, High-level view, LeavePaidLopServiceImpl integration gap, Sandwich expansion algorithm and rule precedence (+1 more)

### Community 104 - "CheckInResponse"
Cohesion: 0.08
Nodes (5): AttendanceCalendarResponse, CheckInResponse, AttendanceMapper, DateTimeUtil, TimeUtil

### Community 106 - "LeaveTransaction"
Cohesion: 0.17
Nodes (3): LeaveTransaction, LeaveTransactionMapper, LeaveTransactionRepository

### Community 108 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.10
Nodes (5): FileStorageServiceB2, UpcomingBirthdayResponse, NotificationGroupKey, NotificationServiceImpl, UpcomingBirthday

### Community 114 - "LeaveTransactionResponse"
Cohesion: 0.10
Nodes (11): LeaveTransactionResponse, LeaveTransactionType, ALLOCATION, ALLOCATION_ADJUSTED, CARRY_FORWARD, EXPIRED, EXPIRY, HR_ADJUSTMENT (+3 more)

### Community 115 - "MonthType"
Cohesion: 0.14
Nodes (13): MonthType, APRIL, AUGUST, DECEMBER, FEBRUARY, JANUARY, JULY, JUNE (+5 more)

### Community 119 - "HolidayResponse"
Cohesion: 0.06
Nodes (12): CreateHolidayRequest, UpdateHolidayRequest, HolidayCalendarResponse, HolidayResponse, HolidayController, HolidayType, HOLIDAY, OPTIONAL_HOLIDAY (+4 more)

### Community 121 - "LeaveApplicationContext"
Cohesion: 0.24
Nodes (3): LeaveRequestRequest, LeaveApplicationContext, LeaveValidationService

### Community 122 - "WebConfig.java"
Cohesion: 0.14
Nodes (3): StringToEnumConverter, StringToEnumConverterFactory, WebConfig

### Community 124 - "LeaveEmailService.java"
Cohesion: 0.18
Nodes (4): AttendanceRegularizationEmailService, EmailService, PasswordResetEmailServiceImpl, PasswordResetEmailService

### Community 128 - "org.springframework.http.ResponseEntity"
Cohesion: 0.07
Nodes (4): Form16Controller, Form16ChallanController, PayrollController, PayslipPdfService

### Community 135 - "LeaveValidationServiceImpl"
Cohesion: 0.16
Nodes (15): 5. Backend - Service Layer (Core Business Logic), Design Review Findings - Resolution Summary, HIGH Severity Findings - All Resolved, Implementation Checklist, MEDIUM Severity Findings - All Addressed, NIT Findings - All Addressed, Summary of Changes, Unit Tests (+7 more)

### Community 138 - "AttendanceBreak"
Cohesion: 0.12
Nodes (3): BreakStartResponse, AttendanceBreak, AttendanceBreakRepository

### Community 145 - "SalaryTemplateResponse"
Cohesion: 0.19
Nodes (3): SalaryTemplateResponse, SalaryTemplateServiceImpl, SalaryTemplateService

### Community 146 - "PayrollAction"
Cohesion: 0.22
Nodes (8): PayrollAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED, SUPERSEDED, UPDATED

### Community 151 - "EmployeeStatus"
Cohesion: 0.29
Nodes (6): EmployeeStatus, ACTIVE, INACTIVE, NOTICE_PERIOD, RESIGNED, TERMINATED

### Community 158 - "graphify"
Cohesion: 0.50
Nodes (3): graphify, C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe, graphify-mcp

### Community 160 - "SalaryComponentType"
Cohesion: 0.50
Nodes (3): SalaryComponentType, DEDUCTION, EARNING

### Community 161 - "TokenType"
Cohesion: 0.50
Nodes (3): TokenType, ACCESS, REFRESH

### Community 205 - "6. Screen-by-screen implementation plan"
Cohesion: 0.33
Nodes (6): 6.1 Employee page — `src/pages/Leave.jsx` (+ `Leave.css`), 6.2 Manager page — `src/pages/LeaveApprovals.jsx` (+ `LeaveApprovals.css`), 6.3 Settings → Leave Types — `src/pages/settings/LeaveTypesSettings.jsx`, 6.4 Dashboard — `src/pages/Dashboard.jsx`, 6.6 Payroll — **cross-module inconsistency to be aware of**, 6. Screen-by-screen implementation plan

## Knowledge Gaps
- **214 isolated node(s):** `APRIL`, `AUGUST`, `DECEMBER`, `FEBRUARY`, `JANUARY` (+209 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1415 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **129 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Employee` connect `Employee` to `lombok.RequiredArgsConstructor`, `AttendanceServiceImpl`, `Form16`, `LeaveAllocationServiceImpl`, `LeaveRequest`, `lombok`, `LeaveApproval`, `EmployeeRepository`, `ResourceNotFoundException`, `Attendance`, `JobTitle`, `org.springframework.security.access.prepost.PreAuthorize`, `CalendarServiceImpl`, `LookupResponse`, `PayrollServiceImpl.java`, `AttendanceRegularizationServiceTest.java`, `org.junit.jupiter.api.DisplayName`, `AttendanceResponse`, `EmployeeServiceImpl.java`, `org.springframework.data.jpa.repository.JpaRepository`, `org.springframework.stereotype.Component`, `PayrollServiceImpl`, `PerformanceReview`, `EmployeePaymentDetailsResponse`, `org.springframework.data.jpa.repository.Query`, `User`, `LeaveBalance`, `org.springframework.stereotype.Service`, `Payroll`, `EmploymentType`, `ValidationException`, `CreatePayrollRequest`, `PerformanceServiceImpl.java`, `Notification`, `SalaryStructure`, `LookupServiceImpl.java`, `SalaryStructureServiceImpl`, `LeaveTransaction`, `org.springframework.transaction.annotation.Transactional`, `EmployeePaymentDetails`, `PayrollHistory`, `LeaveApplicationContext`, `LeaveEmailService.java`, `EmployeeRepository.java`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **What connects `APRIL`, `AUGUST`, `DECEMBER` to the rest of the system?**
  _214 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `io.swagger.v3.oas.annotations.Operation` be split into smaller, more focused modules?**
  _Cohesion score 0.06560283687943262 - nodes in this community are weakly interconnected._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `org.springframework.http.ResponseEntity`, `lombok.RequiredArgsConstructor`, `Employee`, `LeaveAllocationServiceImpl`, `Form16ServiceImpl`, `ErrorCode`, `Form16QuarterResponse`, `LeaveApproval`, `Form16ChallanResponse`, `SalaryTemplateResponse`, `PayslipGenerator`, `JobTitle`, `org.springframework.security.access.prepost.PreAuthorize`, `DepartmentResponse`, `DesignationResponse`, `LeaveRequestMonthFilterTest.java`, `CompanySettings`, `PayrollServiceImpl.java`, `AttendanceRegularizationServiceTest.java`, `org.junit.jupiter.api.DisplayName`, `EmployeeServiceImpl.java`, `AttendanceSettings`, `GlobalExceptionHandler.java`, `org.springframework.stereotype.Component`, `PayrollServiceImpl`, `EmployeePaymentDetailsResponse`, `User`, `Form16Verification`, `LeaveBalance`, `org.springframework.stereotype.Service`, `PerformanceReviewResponse`, `ValidationException`, `LeaveSettings`, `PerformanceServiceImpl.java`, `SalaryStructure`, `Holiday`, `SalaryStructureServiceImpl`, `Form16ExemptionServiceImpl`, `org.springframework.transaction.annotation.Transactional`, `EmployeePaymentDetails`, `HolidayResponse`, `LeaveTypeServiceImpl`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Should `lombok.RequiredArgsConstructor` be split into smaller, more focused modules?**
  _Cohesion score 0.08324324324324324 - nodes in this community are weakly interconnected._
- **Why does `Department` connect `lombok.RequiredArgsConstructor` to `LookupServiceImpl.java`, `LookupResponse`, `Employee`, `lombok`, `org.springframework.stereotype.Service`, `EmployeeServiceImpl.java`, `EmployeeRepository`, `ResourceNotFoundException`, `EmployeeRepository.java`, `DepartmentResponse`, `DesignationResponse`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **Should `Employee` be split into smaller, more focused modules?**
  _Cohesion score 0.09686609686609686 - nodes in this community are weakly interconnected._