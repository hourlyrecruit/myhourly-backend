# Graph Report - my_hourly  (2026-10-10)

## Corpus Check
- 547 files · ~200,435 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 26 file(s) not represented in the graph (top: .csv 9, (none) 6, .properties 6)

## Summary
- 4192 nodes · 13338 edges · 177 communities (89 shown, 88 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 854 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `76a1bdaa`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- lombok.Getter
- ApiResponse
- lombok.RequiredArgsConstructor
- DataInitializer.java
- NotificationType
- PerformanceReview
- org.springframework.transaction.annotation.Transactional
- list
- org.springframework.security.access.prepost.PreAuthorize
- Employee
- Form16ChallanResponse
- ErrorCode
- RoleName
- AttendanceStatus
- Form16QuarterResponse
- org.junit.jupiter.api.DisplayName
- LeaveRequestResponse
- Form16ChapterVIAResponse
- com.my_hourly.employee.entity.Employee
- Form16Section16DeductionResponse
- org.springframework.http.ResponseEntity
- SalaryStructureResponse
- Form16VerificationResponse
- LeaveTransaction
- LeavePaidLopServiceImpl.java
- ValidationException
- LeaveExpiryServiceTest
- EmployeePaymentDetails
- Form16LastFieldsResponse
- PayslipGenerator
- LeaveAllocationServiceImpl
- LeaveTypeResponse
- SalaryTemplateResponse
- JobTitleResponse
- CompanySettings
- DepartmentResponse
- BreakType
- CalendarServiceImpl.java
- PayslipGenerator.java
- ReportController.java
- PayrollHistoryAction
- LeaveRequestManagerActionTest.java
- lombok
- EmployeeResponse
- LeaveApprovalServiceImpl.java
- CustomUserDetails
- Payroll
- AttendanceService
- Form16ExemptionResponse
- GlobalExceptionHandler.java
- LookupResponse
- DesignationResponse
- SlipSample
- Attendance
- AttendanceSettings
- User
- Design Review: Configurable Sandwich Leave Policy
- EmploymentType
- AttendanceRegularization
- Form16EmployerMasterResponse
- JwtServiceImpl.java
- LeaveSettings
- SecurityConfig.java
- RegularizationResponse
- Form16SalaryRequest
- PayrollServiceImpl
- PerformanceServiceImpl.java
- AttendanceServiceImpl
- LeaveActionRequest
- PayrollResponse
- PayrollStatus
- PerformanceController.java
- AttendanceBreak
- SalaryTemplate
- LeaveMonthlyUsageIntegrationTest.java
- LeaveBalance
- RefreshToken
- Form16ServiceImpl
- Form16EmployerMasterServiceImpl
- CalendarController.java
- LeaveRequestQueryStatusTest.java
- AuthenticationController
- Leave PAID / LOP Split & Approver — Frontend Implementation Guide
- Form16Response
- Holiday
- SalaryStructure
- UpdateDraftPayrollRequest
- LeaveTypeController.java
- Graphify Setup and Usage Guide
- MonthType
- PayrollSummaryResponse
- LeavePdfExporter
- org.springframework.data.jpa.domain.Specification
- LeaveBalanceResponse
- AuthenticationServiceImpl.java
- WebConfig.java
- AttendanceRegularizationDetail
- EmployeeServiceImpl.java
- PasswordResetToken
- Form16Salary
- Form16SalaryServiceImpl
- PerformanceReviewResponse
- LeaveTypeServiceImpl
- SecurityUtils
- BaseEntity.java
- org.springframework.context.annotation.Configuration
- LeaveType
- Form16ExemptionServiceImpl
- DesignationServiceImpl
- LeaveTransactionType
- CreatePerformanceReviewRequest
- Form16Section16Deduction
- UpdateSalaryTemplateRequest
- RegularizationDetailStatus
- 6. Screen-by-screen implementation plan
- CheckInResponse
- Form16TaxCalculationService
- PaymentMode
- .calculateAttendance
- JwtAuthenticationFilter.java
- AttendancePdfExporter
- Form16Exemption
- LeaveRequest
- PayrollAction
- AGENTS.md
- CreateSalaryRevisionRequest
- FinancialYearUtil
- MyHourlyApplication.java
- AttendanceMonthlySummaryResponse
- EmployeeStatus
- LeaveRequestRequest
- CreatePayrollRequest
- apply-leave-approvals-edits.mjs
- CheckOutResponse
- ChangePasswordRequest
- LoginRequest
- MarkPayrollPaidRequest
- NumberToWordsConverter
- graphify
- org.springframework.stereotype.Component
- SalaryComponentType
- TokenType
- fix-integration-test.mjs
- AppConstants
- LeaveAllocationType
- SecurityConstants
- AuditEntity.java
- LeaveSpecification.java
- my_hourly/util/SecurityUtils.java
- StringUtils.java
- ValidationUtils.java
- com.my_hourly:my_hourly

## God Nodes (most connected - your core abstractions)
1. `Employee` - 180 edges
2. `ResourceNotFoundException` - 162 edges
3. `ApiResponse` - 125 edges
4. `ErrorCode` - 125 edges
5. `LeaveRequest` - 95 edges
6. `Attendance` - 73 edges
7. `LeaveRequestRepository` - 65 edges
8. `AttendanceStatus` - 63 edges
9. `ValidationException` - 62 edges
10. `User` - 57 edges

## Surprising Connections (you probably didn't know these)
- `HIGH-4: LeaveApplicationContext Field Names Don't Match Usage` --references--> `LeaveApplicationContext`  [INFERRED]
  .agents/tasks/sandwich-leave/design-review.md → src/main/java/com/my_hourly/leave/context/LeaveApplicationContext.java
- `Summary` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/design.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `2. API surface` --references--> `LeaveRequestResponse`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/leave/api/response/LeaveRequestResponse.java
- `6.6 Payroll — **cross-module inconsistency to be aware of**` --references--> `PayrollServiceImpl`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/payroll/service/impl/PayrollServiceImpl.java
- `9. Error handling` --references--> `ApiError`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/common/payload/response/ApiError.java

## Import Cycles
- None detected.

## Communities (177 total, 88 thin omitted)

### Community 0 - "lombok.Getter"
Cohesion: 0.10
Nodes (25): BreakEndRequest, CreateRegularizationDetailRequest, CreateRegularizationRequest, RegularizationDetailActionRequest, RegularizationDetailResponse, AdminRegisterRequest, EmployeeRegisterRequest, GrantRoleRequest (+17 more)

### Community 1 - "ApiResponse"
Cohesion: 0.04
Nodes (7): AdminController, ApiResponse, PageResponse, LeaveTransactionController, DepartmentController, JobTitleController, NotificationController

### Community 2 - "lombok.RequiredArgsConstructor"
Cohesion: 0.07
Nodes (18): AttendanceRegularizationEmailService, EmailService, PasswordResetEmailServiceImpl, DataInitializer, B2FileStorageServiceImpl, LeaveTypeRepository, AttendanceSeeder, CsvReader (+10 more)

### Community 3 - "DataInitializer.java"
Cohesion: 0.05
Nodes (10): LookupMapper, LookupServiceImpl, Department, Designation, JobTitle, DesignationMapper, JobTitleMapper, DepartmentRepository (+2 more)

### Community 4 - "NotificationType"
Cohesion: 0.04
Nodes (40): FileStorageServiceB2, AnnouncementRequest, NotificationResponse, UpcomingBirthdayResponse, Announcement, Notification, NotificationType, ABSENT (+32 more)

### Community 5 - "PerformanceReview"
Cohesion: 0.14
Nodes (5): PerformanceReview, ReviewType, MONTHLY, YEARLY, PerformanceReviewRepository

### Community 6 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.12
Nodes (5): BadRequestException, ResourceNotFoundException, Form16, Form16Repository, PayslipPdfServiceImpl

### Community 7 - "list"
Cohesion: 0.10
Nodes (9): RefreshTokenRepository, Form16ChapterVIARepository, Form16ExemptionRepository, Form16LastFieldsRepository, Form16SalaryRepository, Form16Section16DeductionRepository, HolidayRepository, LeaveRequestMonthAllocationRepository (+1 more)

### Community 8 - "org.springframework.security.access.prepost.PreAuthorize"
Cohesion: 0.05
Nodes (7): Form16LastFieldsController, HolidayController, PayrollController, SalaryStructureController, SalaryTemplateController, PayslipPdfService, SettingController

### Community 9 - "Employee"
Cohesion: 0.08
Nodes (5): BaseEntity, Employee, LeaveAction, APPROVE, REJECT

### Community 10 - "Form16ChallanResponse"
Cohesion: 0.06
Nodes (8): Form16ChallanController, Form16ChallanListResponse, Form16ChallanRequest, Form16ChallanResponse, Form16Challan, Form16ChallanRepository, Form16ChallanService, Form16ChallanServiceImpl

### Community 11 - "ErrorCode"
Cohesion: 0.03
Nodes (63): ErrorCode, ACCESS_DENIED, APPROVAL_PENDING, ATTENDANCE_ALREADY_EXISTS, ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE, ATTENDANCE_DATE_OUT_OF_RANGE, ATTENDANCE_NOT_FOUND, BAD_REQUEST (+55 more)

### Community 12 - "RoleName"
Cohesion: 0.08
Nodes (18): UpdateUserStatusRequest, LoginResponse, UserProfileResponse, RoleName, CLIENT, EMPLOYEE, HR_ADMIN, MANAGER (+10 more)

### Community 13 - "AttendanceStatus"
Cohesion: 0.10
Nodes (18): AttendanceStatus, ABSENT, HALF_DAY, HOLIDAY, LATE, LEAVE, MISSED_CHECKOUT, PRESENT (+10 more)

### Community 14 - "Form16QuarterResponse"
Cohesion: 0.06
Nodes (7): Form16QuarterController, Form16QuarterRequest, Form16QuarterResponse, Form16Quarter, Form16QuarterRepository, Form16QuarterService, Form16QuarterServiceImpl

### Community 15 - "org.junit.jupiter.api.DisplayName"
Cohesion: 0.09
Nodes (6): MonthAllocation, PaidLopAllocation, LeaveBalanceServicePaidDeductionTest, LeavePaidLopAllocationTest, LeaveRequestManagerActionTest, LeaveRequestMapperTest

### Community 16 - "LeaveRequestResponse"
Cohesion: 0.09
Nodes (4): LeaveRequestResponse, LeaveAuthorizationServiceImpl, LeaveRequestServiceImpl, LeaveAuthorizationService

### Community 17 - "Form16ChapterVIAResponse"
Cohesion: 0.07
Nodes (6): Form16ChapterVIAController, Form16ChapterVIARequest, Form16ChapterVIAResponse, Form16ChapterVIA, Form16ChapterVIAService, Form16ChapterVIAServiceImpl

### Community 18 - "com.my_hourly.employee.entity.Employee"
Cohesion: 0.07
Nodes (12): HIGH-1: Critical Architectural Flaw - Storing Expanded Dates Breaks PAID/LOP Calculation, 1.1 Approved days are split into PAID and LOP, 1.2 The monthly allowance decides how many days can be PAID, 1.3 The approver is recorded on the request, 1.4 Submission no longer rejects an over-balance request, 1.5 The balance only moves on PAID days, 1. What changed in the backend, LeaveBalanceRepository (+4 more)

### Community 19 - "Form16Section16DeductionResponse"
Cohesion: 0.08
Nodes (5): Form16Section16DeductionController, Form16Section16DeductionRequest, Form16Section16DeductionResponse, Form16Section16DeductionService, Form16Section16DeductionServiceImpl

### Community 21 - "SalaryStructureResponse"
Cohesion: 0.12
Nodes (4): CreateSalaryStructureRequest, SalaryStructureResponse, SalaryStructureServiceImpl, SalaryStructureService

### Community 22 - "Form16VerificationResponse"
Cohesion: 0.07
Nodes (7): Form16VerificationController, Form16VerificationRequest, Form16VerificationResponse, Form16Verification, Form16VerificationRepository, Form16VerificationService, Form16VerificationServiceImpl

### Community 23 - "LeaveTransaction"
Cohesion: 0.13
Nodes (4): LeaveTransaction, LeaveTransactionMapper, LeaveTransactionRepository, LeaveTransactionServiceImpl

### Community 24 - "LeavePaidLopServiceImpl.java"
Cohesion: 0.06
Nodes (26): 1. Database Schema, 2. Backend - Entity Layer, 3. Backend - DTO Layer, 4. Backend - Mapper Layer, 5. Backend - Service Layer (Core Business Logic), 6. Frontend - Settings UI, Affected Components, Algorithm Details (+18 more)

### Community 25 - "ValidationException"
Cohesion: 0.12
Nodes (4): AttendanceRegularizationServiceImpl, RegularizationValidator, ValidationException, AttendanceRegularizationServiceTest

### Community 26 - "LeaveExpiryServiceTest"
Cohesion: 0.12
Nodes (6): ExpiryEntry, LeaveExpiryPlan, MappedPaidDaysProjection, PaidDaysProjection, LeaveExpiryServiceImpl, LeaveExpiryServiceTest

### Community 27 - "EmployeePaymentDetails"
Cohesion: 0.08
Nodes (6): CreateEmployeePaymentDetailsRequest, EmployeePaymentDetailsResponse, EmployeePaymentDetails, EmployeePaymentDetailsRepository, EmployeePaymentDetailsService, EmployeePaymentDetailsServiceImpl

### Community 28 - "Form16LastFieldsResponse"
Cohesion: 0.09
Nodes (5): Form16LastFieldsRequest, Form16LastFieldsResponse, Form16LastFields, Form16LastFieldsService, Form16LastFieldsServiceImpl

### Community 31 - "LeaveTypeResponse"
Cohesion: 0.12
Nodes (3): LeaveTypeRequest, LeaveTypeResponse, LeaveTypeService

### Community 32 - "SalaryTemplateResponse"
Cohesion: 0.13
Nodes (4): CreateSalaryTemplateRequest, SalaryTemplateResponse, SalaryTemplateServiceImpl, SalaryTemplateService

### Community 33 - "JobTitleResponse"
Cohesion: 0.08
Nodes (5): CreateJobTitleRequest, UpdateJobTitleRequest, JobTitleResponse, JobTitleServiceImpl, JobTitleService

### Community 34 - "CompanySettings"
Cohesion: 0.09
Nodes (7): CompanySettingsRequest, CompanySettingsResponse, CompanySettings, CompanySettingsMapper, CompanySettingsRepository, CompanySettingsService, CompanySettingsServiceImpl

### Community 35 - "DepartmentResponse"
Cohesion: 0.08
Nodes (6): CreateDepartmentRequest, UpdateDepartmentRequest, DepartmentResponse, DepartmentMapper, DepartmentService, DepartmentServiceImpl

### Community 36 - "BreakType"
Cohesion: 0.08
Nodes (10): BreakStartRequest, AttendanceDashboardResponse, BreakStartResponse, BreakType, LUNCH, MEETING, NO_ACTIVE_BREAK, OTHER (+2 more)

### Community 37 - "CalendarServiceImpl.java"
Cohesion: 0.08
Nodes (13): CalendarEventResponse, CalendarResponse, CalendarEventType, ATTENDANCE, BIRTHDAY, HOLIDAY, LEAVE, WORK_ANNIVERSARY (+5 more)

### Community 39 - "ReportController.java"
Cohesion: 0.09
Nodes (13): 6.5 Reports — **backend gap, no frontend change yet**, LeaveStatus, APPROVED, CANCELLED, PENDING, REJECTED, LeaveReportRequest, LeaveReportPageResponse (+5 more)

### Community 40 - "PayrollHistoryAction"
Cohesion: 0.08
Nodes (13): PayrollHistoryResponse, PayrollHistory, PayrollHistoryAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED (+5 more)

### Community 42 - "lombok"
Cohesion: 0.05
Nodes (4): EmployeeStatus, CHECKED_OUT, ON_BREAK, WORKING

### Community 43 - "EmployeeResponse"
Cohesion: 0.07
Nodes (6): EmployeeResponse, EmployeeController, MultipartFileValidator, EmployeeMapper, EmployeeService, EmployeeServiceImpl

### Community 44 - "LeaveApprovalServiceImpl.java"
Cohesion: 0.10
Nodes (9): LeaveApprovalResponse, LeaveApproval, ApprovalLevel, HR, MANAGER, LeaveApprovalMapper, LeaveApprovalRepository, LeaveApprovalServiceImpl (+1 more)

### Community 47 - "AttendanceService"
Cohesion: 0.08
Nodes (4): CheckInRequest, CheckOutRequest, AttendanceController, AttendanceService

### Community 48 - "Form16ExemptionResponse"
Cohesion: 0.09
Nodes (4): Form16ExemptionController, Form16ExemptionRequest, Form16ExemptionResponse, Form16ExemptionService

### Community 49 - "GlobalExceptionHandler.java"
Cohesion: 0.18
Nodes (3): ErrorResponseFactory, GlobalExceptionHandler, ApiError

### Community 50 - "LookupResponse"
Cohesion: 0.08
Nodes (5): EmployeeIdName, LookupResponse, ReportingManagerLookupResponse, LookupController, LookupService

### Community 51 - "DesignationResponse"
Cohesion: 0.07
Nodes (5): DesignationController, CreateDesignationRequest, UpdateDesignationRequest, DesignationResponse, DesignationService

### Community 53 - "Attendance"
Cohesion: 0.08
Nodes (5): AttendanceCalendarResponse, Attendance, AttendanceRepository, AttendanceValidationService, AttendanceValidationServiceImpl

### Community 54 - "AttendanceSettings"
Cohesion: 0.11
Nodes (6): AttendanceSettingsRequest, AttendanceSettings, AttendanceSettingsMapper, AttendanceSettingsRepository, AttendanceSettingsService, AttendanceSettingsServiceImpl

### Community 55 - "User"
Cohesion: 0.07
Nodes (6): User, PasswordResetTokenRepository, UserRepository, AdminServiceImpl, AuthenticationServiceImpl, PasswordResetEmailService

### Community 56 - "Design Review: Configurable Sandwich Leave Policy"
Cohesion: 0.06
Nodes (30): API Contract Backward Compatibility, Correctness Assessment, DB Migration Safety, Design Review: Configurable Sandwich Leave Policy, Executive Summary, Findings, HIGH-2: Missing Method Signature for calculateLeaveDays with forcedWorkingDays, HIGH-3: Ambiguous Double-Counting Prevention Logic (+22 more)

### Community 57 - "EmploymentType"
Cohesion: 0.28
Nodes (5): EmploymentType, CONTRACT, FULL_TIME, INTERN, PART_TIME

### Community 58 - "AttendanceRegularization"
Cohesion: 0.11
Nodes (8): AttendanceRegularization, RegularizationStatus, APPROVED, CANCELLED, PARTIALLY_APPROVED, PENDING, REJECTED, AttendanceRegularizationRepository

### Community 59 - "Form16EmployerMasterResponse"
Cohesion: 0.09
Nodes (4): Form16EmployerMasterController, Form16EmployerMasterRequest, Form16EmployerMasterResponse, Form16EmployerMasterService

### Community 61 - "LeaveSettings"
Cohesion: 0.11
Nodes (7): Implementation Checklist, LeaveSettingsRequest, LeaveSettingsResponse, LeaveSettings, LeaveSettingsMapper, LeaveSettingsServiceImpl, LeaveSettingsService

### Community 62 - "SecurityConfig.java"
Cohesion: 0.11
Nodes (3): SecurityConfig, JwtAccessDeniedHandler, JwtAuthenticationEntryPoint

### Community 63 - "RegularizationResponse"
Cohesion: 0.18
Nodes (3): RegularizationResponse, AttendanceRegularizationController, AttendanceRegularizationService

### Community 64 - "Form16SalaryRequest"
Cohesion: 0.13
Nodes (3): Form16SalaryController, Form16SalaryRequest, Form16SalaryService

### Community 66 - "PerformanceServiceImpl.java"
Cohesion: 0.12
Nodes (10): PerformanceReviewFilterRequest, PerformanceRating, AVERAGE, EXCELLENT, GOOD, NEEDS_IMPROVEMENT, VERY_GOOD, ReviewStatus (+2 more)

### Community 67 - "AttendanceServiceImpl"
Cohesion: 0.13
Nodes (3): AttendanceResponse, AttendanceServiceImpl, AttendanceSpecification

### Community 68 - "LeaveActionRequest"
Cohesion: 0.08
Nodes (4): LeaveActionRequest, LeaveApprovalController, LeaveRequestController, LeaveRequestService

### Community 69 - "PayrollResponse"
Cohesion: 0.13
Nodes (3): RegeneratePayrollRequest, PayrollResponse, PayrollService

### Community 70 - "PayrollStatus"
Cohesion: 0.09
Nodes (8): PayrollStatus, APPROVED, CANCELLED, DRAFT, GENERATED, PAID, SUPERSEDED, PayrollRepository

### Community 71 - "PerformanceController.java"
Cohesion: 0.19
Nodes (3): PerformanceController, UpdatePerformanceReviewRequest, PerformanceService

### Community 72 - "AttendanceBreak"
Cohesion: 0.11
Nodes (4): BreakEndResponse, AttendanceBreak, AttendanceMapper, AttendanceBreakRepository

### Community 75 - "LeaveBalance"
Cohesion: 0.10
Nodes (4): LeaveTransactionResponse, LeaveBalance, LeaveBalanceMapper, LeaveTransactionService

### Community 78 - "Form16EmployerMasterServiceImpl"
Cohesion: 0.15
Nodes (3): Form16EmployerMaster, Form16EmployerMasterRepository, Form16EmployerMasterServiceImpl

### Community 79 - "CalendarController.java"
Cohesion: 0.11
Nodes (4): CalendarController, LeaveAllocationController, LeaveAllocationService, ReportController

### Community 81 - "AuthenticationController"
Cohesion: 0.08
Nodes (5): AuthenticationController, ForgotPasswordRequest, RefreshTokenRequest, ResetPasswordRequest, AuthenticationService

### Community 82 - "Leave PAID / LOP Split & Approver — Frontend Implementation Guide"
Cohesion: 0.10
Nodes (19): Risk 2: Existing leave requests with pending approval, 0. TL;DR — what the frontend actually has to do, 10. Worked examples, 11. Test checklist, 12. Known backend gaps & open questions, 13. Gotchas / checklist, 2. API surface, 3. Response schema — `LeaveRequestResponse` (+11 more)

### Community 83 - "Form16Response"
Cohesion: 0.11
Nodes (3): Form16Controller, Form16Response, Form16Service

### Community 84 - "Holiday"
Cohesion: 0.05
Nodes (14): CreateHolidayRequest, UpdateHolidayRequest, HolidayCalendarResponse, HolidayResponse, Holiday, HolidayType, HOLIDAY, OPTIONAL_HOLIDAY (+6 more)

### Community 85 - "SalaryStructure"
Cohesion: 0.15
Nodes (5): SalaryStructure, SalaryStructureStatus, ACTIVE, INACTIVE, SalaryStructureRepository

### Community 88 - "Graphify Setup and Usage Guide"
Cohesion: 0.11
Nodes (17): 1. Project location, 2. Graphify version and executable, 3. Refresh the graph after code changes, 4. Commands at a glance, 5. Git hooks and when they run, 6. Codebuff MCP configuration, 7. Recommended workflow for AI-assisted code changes, 8. Troubleshooting (+9 more)

### Community 89 - "MonthType"
Cohesion: 0.14
Nodes (13): MonthType, APRIL, AUGUST, DECEMBER, FEBRUARY, JANUARY, JULY, JUNE (+5 more)

### Community 95 - "WebConfig.java"
Cohesion: 0.14
Nodes (3): StringToEnumConverter, StringToEnumConverterFactory, WebConfig

### Community 96 - "AttendanceRegularizationDetail"
Cohesion: 0.18
Nodes (3): AttendanceRegularizationDetail, AttendanceRegularizationMapper, AttendanceRegularizationDetailRepository

### Community 105 - "org.springframework.context.annotation.Configuration"
Cohesion: 0.08
Nodes (6): JpaAuditConfig, JacksonConfig, SchedulingConfig, B2StorageConfig, SwaggerConfig, PasswordConfig

### Community 109 - "LeaveTransactionType"
Cohesion: 0.20
Nodes (9): LeaveTransactionType, ALLOCATION, ALLOCATION_ADJUSTED, CARRY_FORWARD, EXPIRED, EXPIRY, HR_ADJUSTMENT, LEAVE_APPROVED (+1 more)

### Community 114 - "RegularizationDetailStatus"
Cohesion: 0.33
Nodes (5): RegularizationDetailStatus, APPROVED, PENDING, REJECTED, REVERTED

### Community 115 - "6. Screen-by-screen implementation plan"
Cohesion: 0.33
Nodes (6): 6.1 Employee page — `src/pages/Leave.jsx` (+ `Leave.css`), 6.2 Manager page — `src/pages/LeaveApprovals.jsx` (+ `LeaveApprovals.css`), 6.3 Settings → Leave Types — `src/pages/settings/LeaveTypesSettings.jsx`, 6.4 Dashboard — `src/pages/Dashboard.jsx`, 6.6 Payroll — **cross-module inconsistency to be aware of**, 6. Screen-by-screen implementation plan

### Community 118 - "PaymentMode"
Cohesion: 0.33
Nodes (5): PaymentMode, BANK_TRANSFER, CASH, CHEQUE, UPI

### Community 120 - "JwtAuthenticationFilter.java"
Cohesion: 0.09
Nodes (4): RevokedToken, RevokedTokenRepository, JwtAuthenticationFilter, JwtService

### Community 123 - "LeaveRequest"
Cohesion: 0.10
Nodes (3): LeaveEmailService, LeaveRequest, LeaveRequestRepository

### Community 124 - "PayrollAction"
Cohesion: 0.22
Nodes (8): PayrollAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED, SUPERSEDED, UPDATED

### Community 130 - "EmployeeStatus"
Cohesion: 0.29
Nodes (6): EmployeeStatus, ACTIVE, INACTIVE, NOTICE_PERIOD, RESIGNED, TERMINATED

### Community 133 - "LeaveRequestRequest"
Cohesion: 0.20
Nodes (3): LeaveRequestRequest, LeaveApplicationContext, LeaveValidationService

### Community 149 - "graphify"
Cohesion: 0.50
Nodes (3): graphify, C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe, graphify-mcp

### Community 150 - "org.springframework.stereotype.Component"
Cohesion: 0.09
Nodes (16): AttendanceScheduler, DateTimeUtil, EmployeeRepository, LeaveScheduler, LeaveExpiryService, NotificationPriority, HIGH, LOW (+8 more)

### Community 151 - "SalaryComponentType"
Cohesion: 0.50
Nodes (3): SalaryComponentType, DEDUCTION, EARNING

### Community 152 - "TokenType"
Cohesion: 0.50
Nodes (3): TokenType, ACCESS, REFRESH

## Knowledge Gaps
- **214 isolated node(s):** `C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe`, `graphify-mcp`, `FEMALE`, `MALE`, `APPROVED` (+209 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1347 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **88 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Employee` connect `Employee` to `lombok.Getter`, `lombok.RequiredArgsConstructor`, `DataInitializer.java`, `NotificationType`, `LeaveRequestRequest`, `org.springframework.transaction.annotation.Transactional`, `list`, `CreatePayrollRequest`, `PerformanceReview`, `RoleName`, `org.junit.jupiter.api.DisplayName`, `LeaveRequestResponse`, `SalaryStructureResponse`, `org.springframework.stereotype.Component`, `LeaveTransaction`, `ValidationException`, `EmployeePaymentDetails`, `LeaveAllocationServiceImpl`, `CalendarServiceImpl.java`, `PayrollHistoryAction`, `lombok`, `EmployeeResponse`, `LeaveApprovalServiceImpl.java`, `Payroll`, `Attendance`, `User`, `EmploymentType`, `AttendanceRegularization`, `PayrollServiceImpl`, `PerformanceServiceImpl.java`, `AttendanceServiceImpl`, `PayrollStatus`, `LeaveBalance`, `SalaryStructure`, `org.springframework.data.jpa.domain.Specification`, `EmployeeServiceImpl.java`, `PerformanceReviewResponse`, `.calculateAttendance`, `LeaveRequest`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **What connects `C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe`, `graphify-mcp`, `FEMALE` to the rest of the system?**
  _214 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `lombok.Getter` be split into smaller, more focused modules?**
  _Cohesion score 0.09535201640464798 - nodes in this community are weakly interconnected._
- **Why does `ResourceNotFoundException` connect `org.springframework.transaction.annotation.Transactional` to `lombok.Getter`, `lombok.RequiredArgsConstructor`, `NotificationType`, `org.springframework.security.access.prepost.PreAuthorize`, `Employee`, `Form16ChallanResponse`, `ErrorCode`, `Form16QuarterResponse`, `Form16ChapterVIAResponse`, `Form16Section16DeductionResponse`, `org.springframework.http.ResponseEntity`, `SalaryStructureResponse`, `Form16VerificationResponse`, `LeaveTransaction`, `LeavePaidLopServiceImpl.java`, `ValidationException`, `EmployeePaymentDetails`, `Form16LastFieldsResponse`, `LeaveAllocationServiceImpl`, `SalaryTemplateResponse`, `JobTitleResponse`, `CompanySettings`, `DepartmentResponse`, `EmployeeResponse`, `LeaveApprovalServiceImpl.java`, `GlobalExceptionHandler.java`, `AttendanceSettings`, `User`, `LeaveSettings`, `PayrollServiceImpl`, `PerformanceServiceImpl.java`, `Form16ServiceImpl`, `Form16EmployerMasterServiceImpl`, `Holiday`, `SalaryStructure`, `AuthenticationServiceImpl.java`, `EmployeeServiceImpl.java`, `Form16SalaryServiceImpl`, `PerformanceReviewResponse`, `LeaveTypeServiceImpl`, `Form16ExemptionServiceImpl`, `DesignationServiceImpl`, `.calculateAttendance`?**
  _High betweenness centrality (0.049) - this node is a cross-community bridge._
- **Should `ApiResponse` be split into smaller, more focused modules?**
  _Cohesion score 0.03996473699676756 - nodes in this community are weakly interconnected._
- **Why does `ErrorCode` connect `ErrorCode` to `lombok.Getter`, `EmployeeServiceImpl.java`, `lombok.RequiredArgsConstructor`, `PerformanceServiceImpl.java`, `org.springframework.transaction.annotation.Transactional`, `Employee`, `LeaveApprovalServiceImpl.java`, `GlobalExceptionHandler.java`, `org.springframework.http.ResponseEntity`, `AttendanceSettings`, `SecurityConfig.java`, `LeavePaidLopServiceImpl.java`, `ValidationException`, `AuthenticationServiceImpl.java`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Should `lombok.RequiredArgsConstructor` be split into smaller, more focused modules?**
  _Cohesion score 0.06970740103270223 - nodes in this community are weakly interconnected._