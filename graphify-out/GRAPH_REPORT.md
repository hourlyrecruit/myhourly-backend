# Graph Report - my_hourly  (2026-10-10)

## Corpus Check
- 553 files · ~210,139 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 26 file(s) not represented in the graph (top: .csv 9, (none) 6, .properties 6)

## Summary
- 4297 nodes · 13531 edges · 202 communities (88 shown, 114 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 908 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `243e1606`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- JobTitleResponse
- org.springframework.http.ResponseEntity
- org.junit.jupiter.api.Test
- io.swagger.v3.oas.annotations.Operation
- LeaveRequest
- lombok.extern.slf4j.Slf4j
- ErrorCode
- org.springframework.transaction.annotation.Transactional
- NotificationType
- org.springframework.context.annotation.Configuration
- com.my_hourly.employee.entity.Employee
- RoleName
- Form16ChapterVIAResponse
- Form16ChallanResponse
- LeaveReportRequest
- LeaveRequestManagerActionTest.java
- Form16EmployerMasterResponse
- Form16QuarterResponse
- Form16VerificationResponse
- EmployeeResponse
- EmployeePaymentDetailsResponse
- org.springframework.stereotype.Component
- Form16Section16DeductionResponse
- Form16LastFieldsResponse
- notnull
- Form16SalaryRequest
- io.swagger.v3.oas.annotations.tags.Tag
- PayslipGenerator
- LeaveSettings
- lombok.Getter
- CalendarServiceImpl
- Form16ExemptionResponse
- DepartmentResponse
- JobTitle
- Leave PAID / LOP Split & Approver — Frontend Implementation Guide
- Employee
- Technical Design: Configurable Sandwich Leave Policy
- LeaveRequestResponse
- AttendanceReportServiceImpl.java
- SalaryTemplateResponse
- CompanySettings
- ApiResponse
- SalaryStructureResponse
- LeaveExpiryServiceTest
- UserProfileResponse
- DesignationResponse
- PayrollResponse
- PayrollHistoryAction
- LeaveApproval
- PayslipGeneratorOld
- AttendanceSettings
- BaseEntity.java
- Design Review: Configurable Sandwich Leave Policy (Re-review #2)
- Form16Response
- LookupResponse
- PayrollServiceImpl
- GlobalExceptionHandler.java
- org.springframework.data.jpa.repository.JpaRepository
- BreakType
- CalendarController.java
- LeaveTypeResponse
- SlipSample
- LeavePdfExporter
- JwtAccessDeniedHandler.java
- LeaveMonthlyUsageIntegrationTest.java
- PayslipGenerator.java
- Sandwich Leave Policy - Implementation Verification
- LeaveTransactionResponse
- NotificationController
- LeaveTransaction
- PerformanceReview
- AttendanceServiceImpl.java
- .getCurrentEmployee
- JwtServiceImpl.java
- Attendance
- AttendanceServiceImpl
- DesignationController
- list
- .totalDays
- Announcement
- AttendanceStatus
- LeaveType
- CreatePayrollRequest
- Payroll
- SalaryStructure
- io.swagger.v3.oas.annotations.media.Schema
- AttendanceMapper
- SalaryStructureServiceImpl
- LeaveTypeController.java
- Graphify Setup and Usage Guide
- PerformanceReviewResponse
- LeaveSettingsRequest
- EmployeePaymentDetails
- Holiday
- LeaveBalanceResponse
- PerformanceRating
- RefreshToken
- AuthenticationServiceImpl
- DepartmentController
- PerformanceServiceImpl
- ioexception
- B2FileStorageServiceImpl.java
- AttendanceRegularizationDetail
- ChangePasswordRequest
- MonthType
- SalaryTemplate
- LeaveSandwichPolicyTest
- DataInitializer.java
- org.springframework.data.jpa.domain.Specification
- CustomUserDetails
- AttendanceRegularizationServiceTest.java
- AuthenticationController
- HolidayResponse
- HolidayServiceImpl
- SecurityUtils
- LeaveTypeServiceImpl
- WebConfig.java
- LoginRequest
- PasswordResetToken
- HolidayType
- LeaveBalance
- LeaveAllocationServiceImpl
- AttendanceCalendarResponse
- Form16TaxCalculationService
- CreateHolidayRequest
- AttendanceBreak
- PayrollAction
- LeaveRequestQueryStatusTest.java
- RevokedToken
- FinancialYearUtil
- lombok
- Form16SalaryServiceImpl
- PayrollStatus
- B2StorageConfig.java
- EmployeeStatus
- UpdateHolidayRequest
- apply-leave-approvals-edits.mjs
- CreateSalaryRevisionRequest
- LeaveReportRequest.java
- User
- NumberToWordsConverter
- JwtService
- graphify
- SalaryComponentType
- TokenType
- fix-integration-test.mjs
- AppConstants
- LeaveAllocationType
- SecurityConstants
- AGENTS.md
- AuditEntity.java
- LeaveSpecification.java
- my_hourly/util/SecurityUtils.java
- StringUtils.java
- ValidationUtils.java
- com.my_hourly:my_hourly
- Form16Salary
- Form16ExemptionServiceImpl
- EmployeePaymentDetailsServiceImpl
- LeaveSettingsResponse
- Implementation Checklist
- StringToEnumConverterFactory
- Form16Exemption
- LeaveTransactionType
- Backend (Java/Spring Boot)
- SwaggerConfig.java
- BusinessException
- UpdateSalaryTemplateRequest
- FailedPayroll
- LeaveReportSpecification.java
- Implementation Plan: Configurable Sandwich Leave Policy
- ErrorResponseFactory.java

## God Nodes (most connected - your core abstractions)
1. `Employee` - 178 edges
2. `ResourceNotFoundException` - 162 edges
3. `ApiResponse` - 125 edges
4. `ErrorCode` - 125 edges
5. `LeaveRequest` - 103 edges
6. `Attendance` - 73 edges
7. `AttendanceStatus` - 63 edges
8. `ValidationException` - 62 edges
9. `LeaveRequestRepository` - 59 edges
10. `User` - 57 edges

## Surprising Connections (you probably didn't know these)
- `HIGH Severity Findings - All Resolved` --references--> `workingDays()`  [INFERRED]
  .agents/tasks/sandwich-leave/design.md → src/main/java/com/my_hourly/leave/service/impl/LeavePaidLopServiceImpl.java
- `Verification Summary` --references--> `LeaveSandwichPolicyTest`  [INFERRED]
  .agents/tasks/sandwich-leave/plan.md → src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
- `1. Original Dates Preserved` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/verification.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `PAID/LOP Split (Not in Worktree)` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/verification.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `2. API surface` --references--> `LeaveRequestResponse`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/leave/api/response/LeaveRequestResponse.java

## Import Cycles
- None detected.

## Communities (202 total, 114 thin omitted)

### Community 0 - "JobTitleResponse"
Cohesion: 0.12
Nodes (3): JobTitleController, JobTitleResponse, JobTitleService

### Community 1 - "org.springframework.http.ResponseEntity"
Cohesion: 0.04
Nodes (9): EmployeeController, Form16LastFieldsController, Form16ChallanController, LeaveTransactionController, PayrollController, SalaryStructureController, SalaryTemplateController, PayslipPdfService (+1 more)

### Community 2 - "org.junit.jupiter.api.Test"
Cohesion: 0.08
Nodes (5): MonthAllocation, PaidLopAllocation, LeaveBalanceServicePaidDeductionTest, LeavePaidLopAllocationTest, LeaveRequestManagerActionTest

### Community 3 - "io.swagger.v3.oas.annotations.Operation"
Cohesion: 0.06
Nodes (4): AttendanceRegularizationController, Form16EmployerMasterController, HolidayController, EmployeePaymentDetailsController

### Community 4 - "LeaveRequest"
Cohesion: 0.04
Nodes (8): 12. Known backend gaps & open questions, Appendix — files touched by the backend change, LeaveRequest, TypeReference<Set<LocalDate>>, LeaveRequestMapper, LeaveRequestRepository, LeaveAuthorizationServiceImpl, LeaveRequestServiceImpl

### Community 5 - "lombok.extern.slf4j.Slf4j"
Cohesion: 0.05
Nodes (18): BaseEntity, LeaveTypeRepository, LookupMapper, Department, Designation, DepartmentRepository, DesignationRepository, AttendanceSeeder (+10 more)

### Community 6 - "ErrorCode"
Cohesion: 0.03
Nodes (60): ErrorCode, ACCESS_DENIED, APPROVAL_PENDING, ATTENDANCE_ALREADY_EXISTS, ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE, ATTENDANCE_DATE_OUT_OF_RANGE, ATTENDANCE_NOT_FOUND, BAD_REQUEST (+52 more)

### Community 7 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.08
Nodes (9): BadRequestException, DuplicateResourceException, ResourceNotFoundException, Form16, Form16Repository, Form16CertificateNumberGenerator, Form16ServiceImpl, LeaveApprovalServiceImpl (+1 more)

### Community 8 - "NotificationType"
Cohesion: 0.05
Nodes (40): AnnouncementRequest, NotificationResponse, UpcomingBirthdayResponse, Notification, NotificationPriority, HIGH, LOW, MEDIUM (+32 more)

### Community 9 - "org.springframework.context.annotation.Configuration"
Cohesion: 0.18
Nodes (4): JpaAuditConfig, JacksonConfig, SchedulingConfig, PasswordConfig

### Community 10 - "com.my_hourly.employee.entity.Employee"
Cohesion: 0.06
Nodes (16): LeavePaidLopServiceImpl Integration Verification, Unverified/Wrong Assumptions, LeaveApplicationContext, LeaveBalanceRepository, LeaveBalanceServiceImpl, alreadyPaidDaysInMonth(), classify(), holidayDates() (+8 more)

### Community 11 - "RoleName"
Cohesion: 0.06
Nodes (14): RoleName, CLIENT, EMPLOYEE, HR_ADMIN, MANAGER, PAYROLL_ADMIN, SUPER_ADMIN, UserRepository (+6 more)

### Community 12 - "Form16ChapterVIAResponse"
Cohesion: 0.07
Nodes (6): Form16ChapterVIAController, Form16ChapterVIARequest, Form16ChapterVIAResponse, Form16ChapterVIA, Form16ChapterVIAService, Form16ChapterVIAServiceImpl

### Community 13 - "Form16ChallanResponse"
Cohesion: 0.07
Nodes (7): Form16ChallanListResponse, Form16ChallanRequest, Form16ChallanResponse, Form16Challan, Form16ChallanRepository, Form16ChallanService, Form16ChallanServiceImpl

### Community 14 - "LeaveReportRequest"
Cohesion: 0.12
Nodes (10): LeaveStatus, APPROVED, CANCELLED, PENDING, REJECTED, ReportController, LeaveReportRequest, LeaveExcelExporter (+2 more)

### Community 16 - "Form16EmployerMasterResponse"
Cohesion: 0.08
Nodes (5): Form16EmployerMasterRequest, Form16EmployerMasterResponse, Form16EmployerMaster, Form16EmployerMasterService, Form16EmployerMasterServiceImpl

### Community 17 - "Form16QuarterResponse"
Cohesion: 0.06
Nodes (7): Form16QuarterController, Form16QuarterRequest, Form16QuarterResponse, Form16Quarter, Form16QuarterRepository, Form16QuarterService, Form16QuarterServiceImpl

### Community 18 - "Form16VerificationResponse"
Cohesion: 0.07
Nodes (7): Form16VerificationController, Form16VerificationRequest, Form16VerificationResponse, Form16Verification, Form16VerificationRepository, Form16VerificationService, Form16VerificationServiceImpl

### Community 19 - "EmployeeResponse"
Cohesion: 0.12
Nodes (10): CreateEmployeeRequest, UpdateEmployeeByEmployeeRequest, UpdateEmployeeRequest, EmployeeResponse, EmploymentType, CONTRACT, FULL_TIME, INTERN (+2 more)

### Community 20 - "EmployeePaymentDetailsResponse"
Cohesion: 0.10
Nodes (8): CreateEmployeePaymentDetailsRequest, EmployeePaymentDetailsResponse, PaymentMode, BANK_TRANSFER, CASH, CHEQUE, UPI, EmployeePaymentDetailsService

### Community 21 - "org.springframework.stereotype.Component"
Cohesion: 0.11
Nodes (12): AttendanceScheduler, UserMapper, EmployeeRepository, LeaveScheduler, LeaveExpiryService, BirthdayScheduler, HolidayScheduler, NotificationScheduler (+4 more)

### Community 22 - "Form16Section16DeductionResponse"
Cohesion: 0.07
Nodes (6): Form16Section16DeductionController, Form16Section16DeductionRequest, Form16Section16DeductionResponse, Form16Section16Deduction, Form16Section16DeductionService, Form16Section16DeductionServiceImpl

### Community 23 - "Form16LastFieldsResponse"
Cohesion: 0.10
Nodes (5): Form16LastFieldsRequest, Form16LastFieldsResponse, Form16LastFields, Form16LastFieldsService, Form16LastFieldsServiceImpl

### Community 24 - "notnull"
Cohesion: 0.13
Nodes (3): Gender, FEMALE, MALE

### Community 25 - "Form16SalaryRequest"
Cohesion: 0.14
Nodes (3): Form16SalaryController, Form16SalaryRequest, Form16SalaryService

### Community 28 - "LeaveSettings"
Cohesion: 0.19
Nodes (3): LeaveSettings, LeaveSettingsMapper, LeaveSettingsServiceImpl

### Community 29 - "lombok.Getter"
Cohesion: 0.13
Nodes (17): BreakEndRequest, AdminRegisterRequest, EmployeeRegisterRequest, GrantRoleRequest, UpdateUserStatusRequest, LoginResponse, RefreshTokenResponse, RegisterResponse (+9 more)

### Community 30 - "CalendarServiceImpl"
Cohesion: 0.09
Nodes (13): CalendarEventResponse, CalendarResponse, CalendarEventType, ATTENDANCE, BIRTHDAY, HOLIDAY, LEAVE, WORK_ANNIVERSARY (+5 more)

### Community 31 - "Form16ExemptionResponse"
Cohesion: 0.10
Nodes (4): Form16ExemptionController, Form16ExemptionRequest, Form16ExemptionResponse, Form16ExemptionService

### Community 32 - "DepartmentResponse"
Cohesion: 0.08
Nodes (6): CreateDepartmentRequest, UpdateDepartmentRequest, DepartmentResponse, DepartmentMapper, DepartmentService, DepartmentServiceImpl

### Community 33 - "JobTitle"
Cohesion: 0.08
Nodes (6): CreateJobTitleRequest, UpdateJobTitleRequest, JobTitle, JobTitleMapper, JobTitleRepository, JobTitleServiceImpl

### Community 34 - "Leave PAID / LOP Split & Approver — Frontend Implementation Guide"
Cohesion: 0.07
Nodes (24): 0. TL;DR — what the frontend actually has to do, 10. Worked examples, 11. Test checklist, 13. Gotchas / checklist, 1.1 Approved days are split into PAID and LOP, 1.2 The monthly allowance decides how many days can be PAID, 1.3 The approver is recorded on the request, 1.4 Submission no longer rejects an over-balance request (+16 more)

### Community 35 - "Employee"
Cohesion: 0.10
Nodes (4): Employee, EmployeeMapper, EmployeeServiceImpl, EmployeeSpecification

### Community 36 - "Technical Design: Configurable Sandwich Leave Policy"
Cohesion: 0.08
Nodes (23): 1. Database Schema, 2. Backend - Entity Layer, 3. Backend - DTO Layer, 4. Backend - Mapper Layer, 5. Backend - Service Layer (Core Business Logic), 6. Frontend - Settings UI, Affected Components, Algorithm Details (+15 more)

### Community 37 - "LeaveRequestResponse"
Cohesion: 0.06
Nodes (6): LeaveActionRequest, LeaveRequestResponse, LeaveApprovalController, LeaveRequestController, LeaveAuthorizationService, LeaveRequestService

### Community 38 - "AttendanceReportServiceImpl.java"
Cohesion: 0.16
Nodes (6): AttendanceReportRequest, AttendanceExcelExporter, AttendancePdfExporter, AttendanceReportService, AttendanceReportServiceImpl, AttendanceReportSpecification

### Community 39 - "SalaryTemplateResponse"
Cohesion: 0.13
Nodes (4): CreateSalaryTemplateRequest, SalaryTemplateResponse, SalaryTemplateServiceImpl, SalaryTemplateService

### Community 40 - "CompanySettings"
Cohesion: 0.09
Nodes (7): CompanySettingsRequest, CompanySettingsResponse, CompanySettings, CompanySettingsMapper, CompanySettingsRepository, CompanySettingsService, CompanySettingsServiceImpl

### Community 41 - "ApiResponse"
Cohesion: 0.06
Nodes (6): AttendanceResponse, AttendanceController, AttendanceService, AdminController, AdminService, ApiResponse

### Community 42 - "SalaryStructureResponse"
Cohesion: 0.15
Nodes (3): CreateSalaryStructureRequest, SalaryStructureResponse, SalaryStructureService

### Community 43 - "LeaveExpiryServiceTest"
Cohesion: 0.12
Nodes (6): ExpiryEntry, LeaveExpiryPlan, MappedPaidDaysProjection, PaidDaysProjection, LeaveExpiryServiceImpl, LeaveExpiryServiceTest

### Community 44 - "UserProfileResponse"
Cohesion: 0.13
Nodes (8): UserProfileResponse, UserStatus, ACTIVE, DISABLED, INACTIVE, LOCKED, PASSWORD_EXPIRED, AuthenticationMapper

### Community 45 - "DesignationResponse"
Cohesion: 0.08
Nodes (6): CreateDesignationRequest, UpdateDesignationRequest, DesignationResponse, DesignationMapper, DesignationService, DesignationServiceImpl

### Community 46 - "PayrollResponse"
Cohesion: 0.10
Nodes (4): RegeneratePayrollRequest, UpdateDraftPayrollRequest, PayrollResponse, PayrollService

### Community 47 - "PayrollHistoryAction"
Cohesion: 0.08
Nodes (13): PayrollHistoryResponse, PayrollHistory, PayrollHistoryAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED (+5 more)

### Community 48 - "LeaveApproval"
Cohesion: 0.06
Nodes (15): AttendanceRegularizationEmailService, EmailService, PasswordResetEmailServiceImpl, PasswordResetEmailService, LeaveApprovalResponse, LeaveEmailService, LeaveApproval, ApprovalLevel (+7 more)

### Community 50 - "AttendanceSettings"
Cohesion: 0.11
Nodes (5): AttendanceSettingsRequest, AttendanceSettings, AttendanceSettingsMapper, AttendanceSettingsService, AttendanceSettingsServiceImpl

### Community 52 - "Design Review: Configurable Sandwich Leave Policy (Re-review #2)"
Cohesion: 0.07
Nodes (27): 1. No-Double-Counting Algorithm, 2. Integration Completeness, 3. DB Migration Safety, 4. API Contract Backward Compatibility, 5. Test Coverage, 6. Were Previous Findings Addressed?, Algorithm Verification, Attendance Overlap Check Verification (+19 more)

### Community 53 - "Form16Response"
Cohesion: 0.11
Nodes (3): Form16Controller, Form16Response, Form16Service

### Community 54 - "LookupResponse"
Cohesion: 0.09
Nodes (5): EmployeeIdName, LookupResponse, ReportingManagerLookupResponse, LookupController, LookupService

### Community 57 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.16
Nodes (7): Form16ChapterVIARepository, Form16EmployerMasterRepository, Form16LastFieldsRepository, Form16SalaryRepository, Form16Section16DeductionRepository, LeaveRequestMonthAllocationRepository, AttendanceSettingsRepository

### Community 58 - "BreakType"
Cohesion: 0.11
Nodes (9): AttendanceDashboardResponse, BreakStartResponse, BreakType, LUNCH, MEETING, NO_ACTIVE_BREAK, OTHER, PERSONAL (+1 more)

### Community 59 - "CalendarController.java"
Cohesion: 0.16
Nodes (3): CalendarController, LeaveAllocationController, LeaveAllocationService

### Community 66 - "Sandwich Leave Policy - Implementation Verification"
Cohesion: 0.11
Nodes (18): 1. Original Dates Preserved, 2. Forced Working Days Storage, 3. Rule Precedence, 4. Backward Compatibility, 5. Null-Safe Defaults, Architecture Decisions, Backend, Compilation (+10 more)

### Community 69 - "LeaveTransaction"
Cohesion: 0.17
Nodes (3): LeaveTransaction, LeaveTransactionMapper, LeaveTransactionRepository

### Community 70 - "PerformanceReview"
Cohesion: 0.14
Nodes (5): PerformanceReview, ReviewType, MONTHLY, YEARLY, PerformanceReviewRepository

### Community 71 - "AttendanceServiceImpl.java"
Cohesion: 0.10
Nodes (4): BreakStartRequest, CheckInRequest, CheckOutRequest, TimeUtil

### Community 72 - ".getCurrentEmployee"
Cohesion: 0.08
Nodes (12): AttendanceRegularization, RegularizationStatus, APPROVED, CANCELLED, PARTIALLY_APPROVED, PENDING, REJECTED, AttendanceRegularizationMapper (+4 more)

### Community 74 - "Attendance"
Cohesion: 0.10
Nodes (5): Attendance, AttendanceRepository, AttendanceValidationService, AttendanceValidationServiceImpl, ValidationException

### Community 78 - ".totalDays"
Cohesion: 0.11
Nodes (17): Risk 1: Double-counting in edge cases, Risk 2: Existing leave requests with pending approval, Risk 3: Integration with LeavePaidLopServiceImpl, Risk 4: UI confusion - displayed date range vs selected date range, Risk 5: Performance impact on leave validation, Risk 6: Database migration rollback, Risk 7: Attendance marking for sandwich weekends, Risk Assessment and Mitigation (+9 more)

### Community 79 - "Announcement"
Cohesion: 0.15
Nodes (3): Announcement, AnnouncementRepository, AnnouncementService

### Community 80 - "AttendanceStatus"
Cohesion: 0.08
Nodes (11): CheckInResponse, CheckOutResponse, AttendanceStatus, ABSENT, HALF_DAY, HOLIDAY, LATE, LEAVE (+3 more)

### Community 81 - "LeaveType"
Cohesion: 0.14
Nodes (3): LeaveTypeRequest, LeaveType, LeaveTypeMapper

### Community 84 - "SalaryStructure"
Cohesion: 0.16
Nodes (5): SalaryStructure, SalaryStructureStatus, ACTIVE, INACTIVE, SalaryStructureRepository

### Community 85 - "io.swagger.v3.oas.annotations.media.Schema"
Cohesion: 0.18
Nodes (7): 6.5 Reports — **backend gap, no frontend change yet**, AttendanceReportPageResponse, AttendanceReportResponse, AttendanceSummaryResponse, LeaveReportPageResponse, LeaveReportResponse, LeaveSummaryResponse

### Community 89 - "Graphify Setup and Usage Guide"
Cohesion: 0.11
Nodes (17): 1. Project location, 2. Graphify version and executable, 3. Refresh the graph after code changes, 4. Commands at a glance, 5. Git hooks and when they run, 6. Codebuff MCP configuration, 7. Recommended workflow for AI-assisted code changes, 8. Troubleshooting (+9 more)

### Community 90 - "PerformanceReviewResponse"
Cohesion: 0.16
Nodes (3): PerformanceController, PerformanceReviewResponse, PerformanceService

### Community 91 - "LeaveSettingsRequest"
Cohesion: 0.13
Nodes (9): Backend implementation of configurable Sandwich Leave policy, DTO fields marked @NotNull in LeaveSettingsRequest break backward compatibility, Empty file artifact `Mon-only)` in commit, Forced working days persisted as JSON in LeaveRequest entity, Frontend Settings UI missing from commit, High-level view, LeavePaidLopServiceImpl integration gap, Sandwich expansion algorithm and rule precedence (+1 more)

### Community 95 - "PerformanceRating"
Cohesion: 0.12
Nodes (10): PerformanceReviewFilterRequest, PerformanceRating, AVERAGE, EXCELLENT, GOOD, NEEDS_IMPROVEMENT, VERY_GOOD, ReviewStatus (+2 more)

### Community 105 - "MonthType"
Cohesion: 0.14
Nodes (13): MonthType, APRIL, AUGUST, DECEMBER, FEBRUARY, JANUARY, JULY, JUNE (+5 more)

### Community 107 - "LeaveSandwichPolicyTest"
Cohesion: 0.13
Nodes (4): Test coverage comprehensive with edge cases, LeaveRequestRequest, LeaveValidationService, LeaveSandwichPolicyTest

### Community 108 - "DataInitializer.java"
Cohesion: 0.13
Nodes (3): DataInitializer, HolidayRepository, LeaveSettingsRepository

### Community 111 - "AttendanceRegularizationServiceTest.java"
Cohesion: 0.12
Nodes (11): CreateRegularizationDetailRequest, CreateRegularizationRequest, RegularizationDetailActionRequest, RegularizationDetailResponse, RegularizationResponse, RegularizationDetailStatus, APPROVED, PENDING (+3 more)

### Community 112 - "AuthenticationController"
Cohesion: 0.08
Nodes (5): AuthenticationController, ForgotPasswordRequest, RefreshTokenRequest, ResetPasswordRequest, AuthenticationService

### Community 120 - "HolidayType"
Cohesion: 0.13
Nodes (6): HolidayCalendarResponse, HolidayType, HOLIDAY, OPTIONAL_HOLIDAY, PUBLIC_HOLIDAY, WEEKEND

### Community 121 - "LeaveBalance"
Cohesion: 0.15
Nodes (3): LeaveBalance, LeaveBalanceMapper, LeaveTransactionServiceImpl

### Community 128 - "PayrollAction"
Cohesion: 0.22
Nodes (8): PayrollAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED, SUPERSEDED, UPDATED

### Community 132 - "lombok"
Cohesion: 0.06
Nodes (4): EmployeeStatus, CHECKED_OUT, ON_BREAK, WORKING

### Community 134 - "PayrollStatus"
Cohesion: 0.17
Nodes (7): PayrollStatus, APPROVED, CANCELLED, DRAFT, GENERATED, PAID, SUPERSEDED

### Community 136 - "EmployeeStatus"
Cohesion: 0.29
Nodes (6): EmployeeStatus, ACTIVE, INACTIVE, NOTICE_PERIOD, RESIGNED, TERMINATED

### Community 144 - "graphify"
Cohesion: 0.50
Nodes (3): graphify, C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe, graphify-mcp

### Community 145 - "SalaryComponentType"
Cohesion: 0.50
Nodes (3): SalaryComponentType, DEDUCTION, EARNING

### Community 146 - "TokenType"
Cohesion: 0.50
Nodes (3): TokenType, ACCESS, REFRESH

### Community 186 - "Implementation Checklist"
Cohesion: 0.33
Nodes (9): Design Review Findings - Resolution Summary, HIGH Severity Findings - All Resolved, Implementation Checklist, MEDIUM Severity Findings - All Addressed, NIT Findings - All Addressed, Summary of Changes, Implementation Steps, 5. Service Layer - Core Logic (+1 more)

### Community 189 - "LeaveTransactionType"
Cohesion: 0.20
Nodes (9): LeaveTransactionType, ALLOCATION, ALLOCATION_ADJUSTED, CARRY_FORWARD, EXPIRED, EXPIRY, HR_ADJUSTMENT, LEAVE_APPROVED (+1 more)

### Community 190 - "Backend (Java/Spring Boot)"
Cohesion: 0.22
Nodes (9): 1. Database Migration, 2. Entity Layer, 3. DTO Layer, 4. Mapper Layer, 6. Test Coverage, Backend (Java/Spring Boot), Changes Implemented, Frontend (React/TypeScript) (+1 more)

### Community 192 - "BusinessException"
Cohesion: 0.22
Nodes (3): BusinessException, ForbiddenException, UnauthorizedException

### Community 196 - "Implementation Plan: Configurable Sandwich Leave Policy"
Cohesion: 0.50
Nodes (3): Implementation Plan: Configurable Sandwich Leave Policy, Notes, Verification Summary

## Knowledge Gaps
- **214 isolated node(s):** `CLIENT`, `EMPLOYEE`, `HR_ADMIN`, `MANAGER`, `PAYROLL_ADMIN` (+209 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1395 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **114 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Employee` connect `Employee` to `LeaveRequest`, `lombok.extern.slf4j.Slf4j`, `lombok`, `org.springframework.transaction.annotation.Transactional`, `NotificationType`, `PayrollStatus`, `RoleName`, `User`, `EmployeeResponse`, `org.springframework.stereotype.Component`, `notnull`, `io.swagger.v3.oas.annotations.tags.Tag`, `CalendarServiceImpl`, `JobTitle`, `LeaveRequestResponse`, `PayrollHistoryAction`, `LeaveApproval`, `PayrollServiceImpl`, `EmployeePaymentDetailsServiceImpl`, `org.springframework.data.jpa.repository.JpaRepository`, `LeaveTransaction`, `PerformanceReview`, `AttendanceServiceImpl.java`, `.getCurrentEmployee`, `Attendance`, `AttendanceServiceImpl`, `list`, `AttendanceStatus`, `CreatePayrollRequest`, `Payroll`, `SalaryStructure`, `SalaryStructureServiceImpl`, `EmployeePaymentDetails`, `PerformanceServiceImpl`, `LeaveSandwichPolicyTest`, `AttendanceRegularizationServiceTest.java`, `LeaveBalance`, `LeaveAllocationServiceImpl`, `AttendanceCalendarResponse`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **What connects `CLIENT`, `EMPLOYEE`, `HR_ADMIN` to the rest of the system?**
  _214 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `JobTitleResponse` be split into smaller, more focused modules?**
  _Cohesion score 0.11822660098522167 - nodes in this community are weakly interconnected._
- **Why does `ResourceNotFoundException` connect `org.springframework.transaction.annotation.Transactional` to `org.springframework.http.ResponseEntity`, `Form16SalaryServiceImpl`, `ErrorCode`, `NotificationType`, `com.my_hourly.employee.entity.Employee`, `RoleName`, `Form16ChapterVIAResponse`, `User`, `Form16ChallanResponse`, `Form16EmployerMasterResponse`, `Form16QuarterResponse`, `Form16VerificationResponse`, `Form16Section16DeductionResponse`, `Form16LastFieldsResponse`, `io.swagger.v3.oas.annotations.tags.Tag`, `LeaveSettings`, `DepartmentResponse`, `JobTitle`, `Employee`, `SalaryTemplateResponse`, `CompanySettings`, `DesignationResponse`, `AttendanceSettings`, `Form16ExemptionServiceImpl`, `GlobalExceptionHandler.java`, `EmployeePaymentDetailsServiceImpl`, `PayrollServiceImpl`, `BusinessException`, `.getCurrentEmployee`, `CreatePayrollRequest`, `SalaryStructureServiceImpl`, `EmployeePaymentDetails`, `AuthenticationServiceImpl`, `PerformanceServiceImpl`, `AttendanceRegularizationServiceTest.java`, `HolidayServiceImpl`, `LeaveTypeServiceImpl`, `LeaveBalance`, `LeaveAllocationServiceImpl`?**
  _High betweenness centrality (0.056) - this node is a cross-community bridge._
- **Should `org.springframework.http.ResponseEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.04088872292755788 - nodes in this community are weakly interconnected._
- **Why does `Designation` connect `lombok.extern.slf4j.Slf4j` to `JobTitle`, `Employee`, `org.springframework.transaction.annotation.Transactional`, `RoleName`, `DataInitializer.java`, `DesignationResponse`, `EmployeeResponse`?**
  _High betweenness centrality (0.039) - this node is a cross-community bridge._
- **Should `org.junit.jupiter.api.Test` be split into smaller, more focused modules?**
  _Cohesion score 0.08468468468468468 - nodes in this community are weakly interconnected._