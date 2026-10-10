# Graph Report - my_hourly  (2026-10-10)

## Corpus Check
- 555 files · ~212,007 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 26 file(s) not represented in the graph (top: .csv 9, (none) 6, .properties 6)

## Summary
- 4368 nodes · 13668 edges · 226 communities (91 shown, 135 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 901 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e532609a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- org.springframework.security.access.prepost.PreAuthorize
- lombok.extern.slf4j.Slf4j
- Form16
- NotificationType
- com.my_hourly.employee.entity.Employee
- LeaveRequest
- LeaveRequestManagerActionTest
- lombok
- ErrorCode
- ValidationException
- AttendanceResponse
- Form16QuarterResponse
- io.swagger.v3.oas.annotations.Operation
- LeaveApprovalServiceImpl.java
- Form16ChapterVIAResponse
- Form16Section16DeductionResponse
- Form16ChallanResponse
- org.springframework.transaction.annotation.Transactional
- lombok.Getter
- Form16EmployerMasterResponse
- org.springframework.stereotype.Component
- Employee
- Form16VerificationResponse
- org.springframework.http.ResponseEntity
- PayslipGenerator
- LeaveExpiryServiceTest
- Attendance
- Form16LastFieldsResponse
- JobTitle
- lombok.RequiredArgsConstructor
- DepartmentResponse
- Department
- LeaveRequestManagerActionTest.java
- PayslipGenerator.java
- CalendarServiceImpl
- LookupServiceImpl.java
- CompanySettings
- AuthenticationServiceImpl.java
- PayrollHistoryAction
- AttendanceRegularizationServiceTest.java
- org.junit.jupiter.api.DisplayName
- LeaveReportRequest
- Leave PAID / LOP Split & Approver — Frontend Implementation Guide
- PayslipGeneratorOld
- .getAttendanceByEmployeeId
- EmployeeServiceImpl.java
- Form16Response
- SalaryTemplateResponse
- AttendanceSettings
- list
- Form16Salary
- JobTitleController
- AttendanceReportServiceImpl.java
- Design Review: Configurable Sandwich Leave Policy (Re-review #2)
- Technical Design: Configurable Sandwich Leave Policy
- SlipSample
- GlobalExceptionHandler.java
- PayrollResponse
- DataInitializer.java
- CheckInRequest
- PayrollServiceImpl
- ReviewType
- AttendanceStatus
- EmployeePaymentDetailsResponse
- PerformanceController
- org.springframework.data.jpa.repository.Query
- LeaveMonthlyUsageIntegrationTest.java
- JwtServiceImpl.java
- AuthenticationServiceImpl
- Form16LastFieldsServiceImpl
- JwtAccessDeniedHandler.java
- com.my_hourly.leave.entity.LeaveBalance
- UserProfileResponse
- ErrorCode.java
- Payroll
- PerformanceReviewResponse
- Sandwich Leave Policy - Implementation Verification
- lombok.Builder
- BreakType
- Form16EmployerMasterServiceImpl
- SalaryStructureResponse
- Risk Assessment and Mitigation
- org.springframework.context.annotation.Configuration
- AttendanceRegularization
- LeaveSettings
- Graphify Setup and Usage Guide
- AttendanceCalendarResponse
- LeaveType
- PayrollSummaryResponse
- PerformanceServiceImpl.java
- LeaveTypeResponse
- Announcement
- SalaryStructure
- CustomUserDetails
- User
- HolidayCalendarResponse
- Holiday
- LeaveBalanceResponse
- SalaryStructureServiceImpl
- Backend implementation of configurable Sandwich Leave policy
- LeaveRequestQueryStatusTest.java
- B2FileStorageServiceImpl.java
- CheckOutResponse
- CreateHolidayRequest
- LeaveTransactionServiceImpl.java
- DepartmentController
- NotificationServiceImpl
- EmployeePaymentDetails
- ioexception
- MarkPayrollPaidRequest
- SecurityConfig.java
- org.springframework.data.jpa.domain.Specification
- LeaveBalance
- MonthType
- SalaryTemplate
- JwtService
- AttendanceRegularizationDetail
- HolidayResponse
- LeaveTypeServiceImpl
- SecurityUtils
- WebConfig.java
- PasswordResetToken
- org.springframework.stereotype.Service
- PayrollStatus
- B2StorageConfig.java
- Notification
- PayrollController
- LookupController.java
- AttendanceServiceImpl
- HolidayServiceImpl
- LeaveAllocationServiceImpl
- EmployeePaymentDetailsServiceImpl
- Form16ServiceImpl
- Implementation Checklist
- StringToEnumConverterFactory
- AttendanceBreak
- Form16TaxCalculationService
- 4. Business rules
- LeaveTransactionType
- LeaveRequestResponse
- Form16EmployerMaster
- com.lowagie.text.pdf.PdfPTable
- SalaryTemplateServiceImpl
- PayrollAction
- CreateSalaryRevisionRequest
- FinancialYearUtil
- ChangePasswordRequest
- EmployeeStatus
- HolidayType
- UpdateSalaryTemplateRequest
- Form16QuarterController
- apply-leave-approvals-edits.mjs
- NumberToWordsConverter
- AttendanceReportSpecification.java
- graphify
- SalaryStructureController
- SalaryComponentType
- TokenType
- fix-integration-test.mjs
- AppConstants
- Form16ChallanController
- LeaveAllocationType
- SecurityConstants
- AGENTS.md
- AuditEntity.java
- LeaveActionRequest
- my_hourly/util/SecurityUtils.java
- StringUtils.java
- ValidationUtils.java
- Form16ChapterVIA
- com.my_hourly:my_hourly
- Form16LastFields
- Form16Section16Deduction
- PaymentMode
- MyHourlyApplication.java
- CreatePayrollRequest
- 6. Screen-by-screen implementation plan
- CreateSalaryTemplateRequest
- RegeneratePayrollRequest
- UpdateDraftPayrollRequest
- .calculateAttendance

## God Nodes (most connected - your core abstractions)
1. `Employee` - 177 edges
2. `ResourceNotFoundException` - 136 edges
3. `ErrorCode` - 121 edges
4. `ApiResponse` - 118 edges
5. `LeaveRequest` - 98 edges
6. `Attendance` - 73 edges
7. `AttendanceStatus` - 63 edges
8. `ValidationException` - 62 edges
9. `User` - 57 edges
10. `LeaveRequestRepository` - 57 edges

## Surprising Connections (you probably didn't know these)
- `Test coverage comprehensive with edge cases` --references--> `LeaveSandwichPolicyTest`  [INFERRED]
  .agents/tasks/sandwich-leave/2025-01-22-033000-code-review.md → src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
- `Verification Summary` --references--> `LeaveSandwichPolicyTest`  [INFERRED]
  .agents/tasks/sandwich-leave/plan.md → src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
- `DTO fields marked @NotNull in LeaveSettingsRequest break backward compatibility` --references--> `LeaveSettingsRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/2025-01-22-033000-code-review.md → src/main/java/com/my_hourly/settings/leave/dto/request/LeaveSettingsRequest.java
- `1. Original Dates Preserved` --references--> `LeaveRequest`  [INFERRED]
  .agents/tasks/sandwich-leave/verification.md → src/main/java/com/my_hourly/leave/entity/LeaveRequest.java
- `9. Error handling` --references--> `ApiError`  [INFERRED]
  docs/leave/LEAVE_PAID_LOP_FRONTEND_GUIDE.md → src/main/java/com/my_hourly/common/payload/response/ApiError.java

## Import Cycles
- None detected.

## Communities (226 total, 135 thin omitted)

### Community 0 - "org.springframework.security.access.prepost.PreAuthorize"
Cohesion: 0.04
Nodes (5): EmployeeController, Form16Controller, Form16ExemptionController, Form16SalaryController, SalaryTemplateController

### Community 1 - "lombok.extern.slf4j.Slf4j"
Cohesion: 0.15
Nodes (14): UserRepository, LeaveTypeRepository, DepartmentRepository, AttendanceSeeder, CsvReader, EmployeeSeeder, UserSeeder, HolidaySeeder (+6 more)

### Community 2 - "Form16"
Cohesion: 0.10
Nodes (4): Form16, Form16Exemption, Form16Repository, Form16CertificateNumberGenerator

### Community 3 - "NotificationType"
Cohesion: 0.08
Nodes (27): NotificationPriority, HIGH, LOW, MEDIUM, NotificationType, ABSENT, ANNOUNCEMENT, APPROVED (+19 more)

### Community 4 - "com.my_hourly.employee.entity.Employee"
Cohesion: 0.07
Nodes (7): 5. Backend - Service Layer (Core Business Logic), 12. Known backend gaps & open questions, LeaveRequestRequest, LeaveApplicationContext, LeavePaidLopServiceImpl, LeaveValidationServiceImpl, LeaveValidationService

### Community 5 - "LeaveRequest"
Cohesion: 0.04
Nodes (12): Risk 3: Integration with LeavePaidLopServiceImpl, Attendance Overlap Validation, Existing Leave Balance Logic, Integration Points, PAID/LOP Split (Not in Worktree), LeaveRequest, TypeReference<Set<LocalDate>>, LeaveRequestMapper (+4 more)

### Community 6 - "LeaveRequestManagerActionTest"
Cohesion: 0.11
Nodes (10): 1.1 Approved days are split into PAID and LOP, 1.2 The monthly allowance decides how many days can be PAID, 1.3 The approver is recorded on the request, 1.4 Submission no longer rejects an over-balance request, 1.5 The balance only moves on PAID days, 1. What changed in the backend, Appendix — files touched by the backend change, MonthAllocation (+2 more)

### Community 7 - "lombok"
Cohesion: 0.06
Nodes (5): EmployeeStatus, CHECKED_OUT, ON_BREAK, WORKING, BaseEntity

### Community 8 - "ErrorCode"
Cohesion: 0.03
Nodes (63): ErrorCode, ACCESS_DENIED, APPROVAL_PENDING, ATTENDANCE_ALREADY_EXISTS, ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE, ATTENDANCE_DATE_OUT_OF_RANGE, ATTENDANCE_NOT_FOUND, BAD_REQUEST (+55 more)

### Community 9 - "ValidationException"
Cohesion: 0.13
Nodes (4): AttendanceRegularizationServiceImpl, RegularizationValidator, ValidationException, AttendanceRegularizationServiceTest

### Community 10 - "AttendanceResponse"
Cohesion: 0.09
Nodes (4): AttendanceResponse, AttendanceController, AttendanceScheduler, AttendanceService

### Community 11 - "Form16QuarterResponse"
Cohesion: 0.07
Nodes (6): Form16QuarterRequest, Form16QuarterResponse, Form16Quarter, Form16QuarterRepository, Form16QuarterService, Form16QuarterServiceImpl

### Community 12 - "io.swagger.v3.oas.annotations.Operation"
Cohesion: 0.04
Nodes (9): AttendanceRegularizationController, AdminController, AuthenticationController, ApiResponse, LeaveBalanceController, LeaveTransactionController, DesignationController, NotificationController (+1 more)

### Community 13 - "LeaveApprovalServiceImpl.java"
Cohesion: 0.10
Nodes (12): LeaveApprovalResponse, LeaveApproval, ApprovalLevel, HR, MANAGER, LeaveAction, APPROVE, REJECT (+4 more)

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
Cohesion: 0.09
Nodes (25): BreakEndRequest, AdminRegisterRequest, EmployeeRegisterRequest, GrantRoleRequest, UpdateUserStatusRequest, RefreshTokenResponse, RegisterResponse, RoleName (+17 more)

### Community 19 - "Form16EmployerMasterResponse"
Cohesion: 0.09
Nodes (4): Form16EmployerMasterController, Form16EmployerMasterRequest, Form16EmployerMasterResponse, Form16EmployerMasterService

### Community 20 - "org.springframework.stereotype.Component"
Cohesion: 0.09
Nodes (10): EmployeeRepository, HolidayRepository, LeaveScheduler, BirthdayScheduler, HolidayScheduler, NotificationScheduler, UpcomingBirthdayScheduler, WorkAnniversaryScheduler (+2 more)

### Community 21 - "Employee"
Cohesion: 0.10
Nodes (6): ResourceNotFoundException, Employee, EmployeeMapper, EmployeeServiceImpl, EmployeeSpecification, LeaveAuthorizationService

### Community 22 - "Form16VerificationResponse"
Cohesion: 0.07
Nodes (7): Form16VerificationController, Form16VerificationRequest, Form16VerificationResponse, Form16Verification, Form16VerificationRepository, Form16VerificationService, Form16VerificationServiceImpl

### Community 23 - "org.springframework.http.ResponseEntity"
Cohesion: 0.12
Nodes (4): LeaveApprovalController, LeaveRequestController, LeaveTypeController, LeaveRequestService

### Community 25 - "LeaveExpiryServiceTest"
Cohesion: 0.12
Nodes (7): ExpiryEntry, LeaveExpiryPlan, MappedPaidDaysProjection, PaidDaysProjection, LeaveExpiryServiceImpl, LeaveExpiryService, LeaveExpiryServiceTest

### Community 26 - "Attendance"
Cohesion: 0.12
Nodes (3): Attendance, AttendanceRepository, AttendanceValidationServiceImpl

### Community 27 - "Form16LastFieldsResponse"
Cohesion: 0.10
Nodes (4): Form16LastFieldsController, Form16LastFieldsRequest, Form16LastFieldsResponse, Form16LastFieldsService

### Community 28 - "JobTitle"
Cohesion: 0.06
Nodes (8): CreateJobTitleRequest, UpdateJobTitleRequest, JobTitleResponse, JobTitle, JobTitleMapper, JobTitleRepository, JobTitleServiceImpl, JobTitleService

### Community 29 - "lombok.RequiredArgsConstructor"
Cohesion: 0.10
Nodes (6): CalendarController, CalendarService, PageResponse, LeaveAllocationController, EmployeePaymentDetailsController, ReportController

### Community 30 - "DepartmentResponse"
Cohesion: 0.08
Nodes (6): CreateDepartmentRequest, UpdateDepartmentRequest, DepartmentResponse, DepartmentMapper, DepartmentService, DepartmentServiceImpl

### Community 31 - "Department"
Cohesion: 0.05
Nodes (9): CreateDesignationRequest, UpdateDesignationRequest, DesignationResponse, Department, Designation, DesignationMapper, DesignationRepository, DesignationService (+1 more)

### Community 34 - "CalendarServiceImpl"
Cohesion: 0.09
Nodes (12): CalendarEventResponse, CalendarResponse, CalendarEventType, ATTENDANCE, BIRTHDAY, HOLIDAY, LEAVE, WORK_ANNIVERSARY (+4 more)

### Community 35 - "LookupServiceImpl.java"
Cohesion: 0.08
Nodes (5): EmployeeIdName, LookupResponse, ReportingManagerLookupResponse, LookupMapper, LookupServiceImpl

### Community 36 - "CompanySettings"
Cohesion: 0.10
Nodes (7): CompanySettingsRequest, CompanySettingsResponse, CompanySettings, CompanySettingsMapper, CompanySettingsRepository, CompanySettingsService, CompanySettingsServiceImpl

### Community 37 - "AuthenticationServiceImpl.java"
Cohesion: 0.05
Nodes (7): ForgotPasswordRequest, LoginRequest, RefreshTokenRequest, ResetPasswordRequest, LoginResponse, UserMapper, AuthenticationService

### Community 38 - "PayrollHistoryAction"
Cohesion: 0.08
Nodes (13): PayrollHistoryResponse, PayrollHistory, PayrollHistoryAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED (+5 more)

### Community 39 - "AttendanceRegularizationServiceTest.java"
Cohesion: 0.11
Nodes (12): CreateRegularizationDetailRequest, CreateRegularizationRequest, RegularizationDetailActionRequest, RegularizationDetailResponse, RegularizationResponse, RegularizationDetailStatus, APPROVED, PENDING (+4 more)

### Community 40 - "org.junit.jupiter.api.DisplayName"
Cohesion: 0.11
Nodes (5): LeaveSpecification, MonthRange, LeavePaidLopAllocationTest, LeaveRequestMonthFilterTest, LeaveSandwichPolicyTest

### Community 41 - "LeaveReportRequest"
Cohesion: 0.16
Nodes (9): LeaveStatus, APPROVED, CANCELLED, PENDING, REJECTED, LeaveReportRequest, LeaveExcelExporter, LeaveReportServiceImpl (+1 more)

### Community 42 - "Leave PAID / LOP Split & Approver — Frontend Implementation Guide"
Cohesion: 0.13
Nodes (16): Risk 2: Existing leave requests with pending approval, 0. TL;DR — what the frontend actually has to do, 10. Worked examples, 11. Test checklist, 13. Gotchas / checklist, 3. Response schema — `LeaveRequestResponse`, 4. Balance semantics — `LeaveBalanceResponse`, 5. Classification rules (server-side — do **not** re-implement in the UI) (+8 more)

### Community 46 - "EmployeeServiceImpl.java"
Cohesion: 0.14
Nodes (14): CreateEmployeeRequest, UpdateEmployeeByEmployeeRequest, UpdateEmployeeRequest, EmployeeDropdownResponse, EmployeeResponse, EmploymentType, CONTRACT, FULL_TIME (+6 more)

### Community 50 - "AttendanceSettings"
Cohesion: 0.11
Nodes (6): AttendanceSettingsRequest, AttendanceSettings, AttendanceSettingsMapper, AttendanceSettingsRepository, AttendanceSettingsService, AttendanceSettingsServiceImpl

### Community 51 - "list"
Cohesion: 0.13
Nodes (6): Form16ChapterVIARepository, Form16ExemptionRepository, Form16LastFieldsRepository, Form16SalaryRepository, Form16Section16DeductionRepository, LeaveRequestMonthAllocationRepository

### Community 52 - "Form16Salary"
Cohesion: 0.06
Nodes (8): Form16ExemptionRequest, Form16ExemptionResponse, Form16SalaryRequest, Form16Salary, Form16ExemptionService, Form16SalaryService, Form16ExemptionServiceImpl, Form16SalaryServiceImpl

### Community 54 - "AttendanceReportServiceImpl.java"
Cohesion: 0.15
Nodes (7): AttendanceReportRequest, AttendanceReportResponse, AttendanceExcelExporter, AttendancePdfExporter, AttendanceReportService, AttendanceReportServiceImpl, AttendanceReportSpecification

### Community 55 - "Design Review: Configurable Sandwich Leave Policy (Re-review #2)"
Cohesion: 0.07
Nodes (29): 1. No-Double-Counting Algorithm, 2. Integration Completeness, 3. DB Migration Safety, 4. API Contract Backward Compatibility, 5. Test Coverage, 6. Were Previous Findings Addressed?, Algorithm Verification, Attendance Overlap Check Verification (+21 more)

### Community 56 - "Technical Design: Configurable Sandwich Leave Policy"
Cohesion: 0.09
Nodes (22): 1. Database Schema, 2. Backend - Entity Layer, 3. Backend - DTO Layer, 4. Backend - Mapper Layer, 6. Frontend - Settings UI, Affected Components, Algorithm Details, Database Migration (+14 more)

### Community 58 - "GlobalExceptionHandler.java"
Cohesion: 0.18
Nodes (3): ErrorResponseFactory, GlobalExceptionHandler, ApiError

### Community 63 - "ReviewType"
Cohesion: 0.17
Nodes (4): ReviewType, MONTHLY, YEARLY, PerformanceReviewRepository

### Community 64 - "AttendanceStatus"
Cohesion: 0.08
Nodes (11): AttendanceDashboardResponse, CheckInResponse, AttendanceStatus, ABSENT, HALF_DAY, HOLIDAY, LATE, LEAVE (+3 more)

### Community 65 - "EmployeePaymentDetailsResponse"
Cohesion: 0.14
Nodes (3): CreateEmployeePaymentDetailsRequest, EmployeePaymentDetailsResponse, EmployeePaymentDetailsService

### Community 71 - "Form16LastFieldsServiceImpl"
Cohesion: 0.09
Nodes (3): Form16ChapterVIAServiceImpl, Form16LastFieldsServiceImpl, Form16Section16DeductionServiceImpl

### Community 72 - "JwtAccessDeniedHandler.java"
Cohesion: 0.11
Nodes (4): JwtAccessDeniedHandler, JwtAuthenticationEntryPoint, JwtAuthenticationFilter, CustomUserDetailsService

### Community 73 - "com.my_hourly.leave.entity.LeaveBalance"
Cohesion: 0.11
Nodes (4): LeaveBalanceRepository, LeaveBalanceServiceImpl, LeaveBalanceService, LeaveBalanceServicePaidDeductionTest

### Community 74 - "UserProfileResponse"
Cohesion: 0.12
Nodes (8): UserProfileResponse, UserStatus, ACTIVE, DISABLED, INACTIVE, LOCKED, PASSWORD_EXPIRED, AuthenticationMapper

### Community 75 - "ErrorCode.java"
Cohesion: 0.18
Nodes (6): BadRequestException, DuplicateResourceException, SalaryStructureStatus, ACTIVE, INACTIVE, LeaveSettingsRepository

### Community 77 - "PerformanceReviewResponse"
Cohesion: 0.14
Nodes (4): CreatePerformanceReviewRequest, PerformanceReviewResponse, PerformanceServiceImpl, PerformanceService

### Community 78 - "Sandwich Leave Policy - Implementation Verification"
Cohesion: 0.07
Nodes (27): 1. Database Migration, 1. Original Dates Preserved, 2. Entity Layer, 2. Forced Working Days Storage, 3. DTO Layer, 3. Rule Precedence, 4. Backward Compatibility, 4. Mapper Layer (+19 more)

### Community 79 - "lombok.Builder"
Cohesion: 0.23
Nodes (7): 6.5 Reports — **backend gap, no frontend change yet**, UpdateSalaryTemplateStatusRequest, AttendanceReportPageResponse, AttendanceSummaryResponse, LeaveReportPageResponse, LeaveReportResponse, LeaveSummaryResponse

### Community 80 - "BreakType"
Cohesion: 0.10
Nodes (9): BreakStartRequest, BreakStartResponse, BreakType, LUNCH, MEETING, NO_ACTIVE_BREAK, OTHER, PERSONAL (+1 more)

### Community 82 - "SalaryStructureResponse"
Cohesion: 0.15
Nodes (3): CreateSalaryStructureRequest, SalaryStructureResponse, SalaryStructureService

### Community 83 - "Risk Assessment and Mitigation"
Cohesion: 0.33
Nodes (6): Risk 1: Double-counting in edge cases, Risk 4: UI confusion - displayed date range vs selected date range, Risk 5: Performance impact on leave validation, Risk 6: Database migration rollback, Risk 7: Attendance marking for sandwich weekends, Risk Assessment and Mitigation

### Community 84 - "org.springframework.context.annotation.Configuration"
Cohesion: 0.12
Nodes (5): JpaAuditConfig, JacksonConfig, SchedulingConfig, SwaggerConfig, PasswordConfig

### Community 85 - "AttendanceRegularization"
Cohesion: 0.12
Nodes (8): AttendanceRegularization, RegularizationStatus, APPROVED, CANCELLED, PARTIALLY_APPROVED, PENDING, REJECTED, AttendanceRegularizationRepository

### Community 86 - "LeaveSettings"
Cohesion: 0.10
Nodes (6): LeaveSettingsRequest, LeaveSettingsResponse, LeaveSettings, LeaveSettingsMapper, LeaveSettingsServiceImpl, LeaveSettingsService

### Community 87 - "Graphify Setup and Usage Guide"
Cohesion: 0.11
Nodes (17): 1. Project location, 2. Graphify version and executable, 3. Refresh the graph after code changes, 4. Commands at a glance, 5. Git hooks and when they run, 6. Codebuff MCP configuration, 7. Recommended workflow for AI-assisted code changes, 8. Troubleshooting (+9 more)

### Community 91 - "PerformanceServiceImpl.java"
Cohesion: 0.13
Nodes (10): PerformanceReviewFilterRequest, PerformanceRating, AVERAGE, EXCELLENT, GOOD, NEEDS_IMPROVEMENT, VERY_GOOD, ReviewStatus (+2 more)

### Community 92 - "LeaveTypeResponse"
Cohesion: 0.12
Nodes (3): LeaveTypeRequest, LeaveTypeResponse, LeaveTypeService

### Community 93 - "Announcement"
Cohesion: 0.11
Nodes (9): NotificationResponse, Announcement, UploadType, MAGAZINE, POST, NotificationMapper, AnnouncementRepository, AnnouncementService (+1 more)

### Community 96 - "User"
Cohesion: 0.11
Nodes (3): RefreshToken, User, RefreshTokenRepository

### Community 101 - "Backend implementation of configurable Sandwich Leave policy"
Cohesion: 0.20
Nodes (9): Backend implementation of configurable Sandwich Leave policy, DTO fields marked @NotNull in LeaveSettingsRequest break backward compatibility, Empty file artifact `Mon-only)` in commit, Forced working days persisted as JSON in LeaveRequest entity, Frontend Settings UI missing from commit, High-level view, LeavePaidLopServiceImpl integration gap, Sandwich expansion algorithm and rule precedence (+1 more)

### Community 104 - "CheckOutResponse"
Cohesion: 0.12
Nodes (4): CheckOutResponse, AttendanceMapper, DateTimeUtil, TimeUtil

### Community 106 - "LeaveTransactionServiceImpl.java"
Cohesion: 0.13
Nodes (4): LeaveTransaction, LeaveTransactionMapper, LeaveTransactionRepository, LeaveTransactionServiceImpl

### Community 108 - "NotificationServiceImpl"
Cohesion: 0.18
Nodes (4): UpcomingBirthdayResponse, NotificationGroupKey, NotificationServiceImpl, UpcomingBirthday

### Community 114 - "LeaveBalance"
Cohesion: 0.12
Nodes (3): LeaveTransactionResponse, LeaveBalance, LeaveTransactionService

### Community 115 - "MonthType"
Cohesion: 0.14
Nodes (13): MonthType, APRIL, AUGUST, DECEMBER, FEBRUARY, JANUARY, JULY, JUNE (+5 more)

### Community 119 - "HolidayResponse"
Cohesion: 0.14
Nodes (3): HolidayResponse, HolidayController, HolidayService

### Community 124 - "org.springframework.stereotype.Service"
Cohesion: 0.18
Nodes (5): AttendanceRegularizationEmailService, EmailService, PasswordResetEmailServiceImpl, LeaveEmailService, PayslipPdfServiceImpl

### Community 125 - "PayrollStatus"
Cohesion: 0.18
Nodes (7): PayrollStatus, APPROVED, CANCELLED, DRAFT, GENERATED, PAID, SUPERSEDED

### Community 135 - "Implementation Checklist"
Cohesion: 0.21
Nodes (12): Design Review Findings - Resolution Summary, HIGH Severity Findings - All Resolved, Implementation Checklist, MEDIUM Severity Findings - All Addressed, NIT Findings - All Addressed, Summary of Changes, Implementation Plan: Configurable Sandwich Leave Policy, Implementation Steps (+4 more)

### Community 138 - "AttendanceBreak"
Cohesion: 0.13
Nodes (3): BreakEndResponse, AttendanceBreak, AttendanceBreakRepository

### Community 140 - "4. Business rules"
Cohesion: 0.14
Nodes (12): 1. Basics, 2. Endpoints you need, 4.1 Working days and sandwich leave, 4.2 PAID vs LOP (important), 4.3 Submission rules (`POST`), 4.4 Approval rules (`MANAGER`), 4.5 Cancellation, 4.6 Status lifecycle (+4 more)

### Community 141 - "LeaveTransactionType"
Cohesion: 0.22
Nodes (9): LeaveTransactionType, ALLOCATION, ALLOCATION_ADJUSTED, CARRY_FORWARD, EXPIRED, EXPIRY, HR_ADJUSTMENT, LEAVE_APPROVED (+1 more)

### Community 142 - "LeaveRequestResponse"
Cohesion: 0.14
Nodes (8): 3. Payloads, Apply — `POST /leave-requests`, Leave settings (`GET/PUT /settings/leave`), `LeaveBalanceResponse`, `LeaveRequestResponse` (the main object), Manager action — `PUT /leave-approvals/{id}/leave-approval-by-manager`, 2. API surface, LeaveRequestResponse

### Community 146 - "PayrollAction"
Cohesion: 0.22
Nodes (8): PayrollAction, APPROVED, CANCELLED, GENERATED, PAID, REGENERATED, SUPERSEDED, UPDATED

### Community 151 - "EmployeeStatus"
Cohesion: 0.29
Nodes (6): EmployeeStatus, ACTIVE, INACTIVE, NOTICE_PERIOD, RESIGNED, TERMINATED

### Community 152 - "HolidayType"
Cohesion: 0.14
Nodes (6): UpdateHolidayRequest, HolidayType, HOLIDAY, OPTIONAL_HOLIDAY, PUBLIC_HOLIDAY, WEEKEND

### Community 158 - "graphify"
Cohesion: 0.50
Nodes (3): graphify, C:\Users\User\AppData\Local\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe, graphify-mcp

### Community 160 - "SalaryComponentType"
Cohesion: 0.50
Nodes (3): SalaryComponentType, DEDUCTION, EARNING

### Community 161 - "TokenType"
Cohesion: 0.50
Nodes (3): TokenType, ACCESS, REFRESH

### Community 202 - "PaymentMode"
Cohesion: 0.25
Nodes (5): PaymentMode, BANK_TRANSFER, CASH, CHEQUE, UPI

### Community 205 - "6. Screen-by-screen implementation plan"
Cohesion: 0.33
Nodes (6): 6.1 Employee page — `src/pages/Leave.jsx` (+ `Leave.css`), 6.2 Manager page — `src/pages/LeaveApprovals.jsx` (+ `LeaveApprovals.css`), 6.3 Settings → Leave Types — `src/pages/settings/LeaveTypesSettings.jsx`, 6.4 Dashboard — `src/pages/Dashboard.jsx`, 6.6 Payroll — **cross-module inconsistency to be aware of**, 6. Screen-by-screen implementation plan

## Knowledge Gaps
- **214 isolated node(s):** `APRIL`, `AUGUST`, `DECEMBER`, `FEBRUARY`, `JANUARY` (+209 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1412 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **135 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Employee` connect `Employee` to `lombok.extern.slf4j.Slf4j`, `AttendanceServiceImpl`, `Form16`, `LeaveAllocationServiceImpl`, `LeaveRequest`, `com.my_hourly.employee.entity.Employee`, `lombok`, `NotificationType`, `ValidationException`, `EmployeePaymentDetailsServiceImpl`, `LeaveApprovalServiceImpl.java`, `org.springframework.transaction.annotation.Transactional`, `lombok.Getter`, `AttendanceServiceImpl.java`, `org.springframework.stereotype.Component`, `Attendance`, `JobTitle`, `lombok.RequiredArgsConstructor`, `Department`, `CalendarServiceImpl`, `LookupServiceImpl.java`, `PayrollHistoryAction`, `AttendanceRegularizationServiceTest.java`, `.getAttendanceByEmployeeId`, `EmployeeServiceImpl.java`, `list`, `PayrollServiceImpl`, `ReviewType`, `AttendanceStatus`, `org.springframework.data.jpa.repository.Query`, `PaymentMode`, `ErrorCode.java`, `Payroll`, `CreatePayrollRequest`, `.calculateAttendance`, `AttendanceRegularization`, `AttendanceCalendarResponse`, `PerformanceServiceImpl.java`, `SalaryStructure`, `User`, `SalaryStructureServiceImpl`, `LeaveTransactionServiceImpl.java`, `NotificationServiceImpl`, `EmployeePaymentDetails`, `org.springframework.data.jpa.domain.Specification`, `LeaveBalance`, `AttendanceRegularizationDetail`, `org.springframework.stereotype.Service`, `PayrollStatus`, `Notification`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **What connects `APRIL`, `AUGUST`, `DECEMBER` to the rest of the system?**
  _214 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `org.springframework.security.access.prepost.PreAuthorize` be split into smaller, more focused modules?**
  _Cohesion score 0.0422360248447205 - nodes in this community are weakly interconnected._
- **Why does `LeaveRequest` connect `LeaveRequest` to `lombok.extern.slf4j.Slf4j`, `AttendanceServiceImpl`, `com.my_hourly.employee.entity.Employee`, `LeaveRequestManagerActionTest`, `Implementation Checklist`, `LeaveApprovalServiceImpl.java`, `lombok.Getter`, `AttendanceServiceImpl.java`, `org.springframework.stereotype.Component`, `Employee`, `Attendance`, `AttendanceReportSpecification.java`, `LeaveRequestManagerActionTest.java`, `org.junit.jupiter.api.DisplayName`, `LeaveReportRequest`, `.getAttendanceByEmployeeId`, `list`, `Technical Design: Configurable Sandwich Leave Policy`, `CheckInRequest`, `org.springframework.data.jpa.repository.Query`, `LeaveMonthlyUsageIntegrationTest.java`, `com.my_hourly.leave.entity.LeaveBalance`, `ErrorCode.java`, `Sandwich Leave Policy - Implementation Verification`, `LeaveTransactionServiceImpl.java`, `NotificationServiceImpl`, `LeaveBalance`, `org.springframework.stereotype.Service`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Should `Form16` be split into smaller, more focused modules?**
  _Cohesion score 0.0967741935483871 - nodes in this community are weakly interconnected._
- **Why does `LeaveValidationServiceImpl` connect `com.my_hourly.employee.entity.Employee` to `LeaveRequestManagerActionTest.java`, `lombok.extern.slf4j.Slf4j`, `Implementation Checklist`, `Design Review: Configurable Sandwich Leave Policy (Re-review #2)`, `Technical Design: Configurable Sandwich Leave Policy`, `org.springframework.stereotype.Service`, `lombok.RequiredArgsConstructor`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **Should `NotificationType` be split into smaller, more focused modules?**
  _Cohesion score 0.07741935483870968 - nodes in this community are weakly interconnected._