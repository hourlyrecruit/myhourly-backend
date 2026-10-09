-- =====================================================================
-- V8 - Query-performance indexes
-- =====================================================================
-- Every index below is backed by a repository method / JPQL query that is
-- actually called from a service, controller or @Scheduled job (see the
-- "Index Optimization Summary" for the query -> index mapping).
--
-- Background:
--   * PostgreSQL does NOT create an index on the referencing side of a
--     foreign key (unlike MySQL/InnoDB), and the baseline schema adds all
--     foreign keys explicitly, so FK columns are unindexed unless we add
--     the index here.
--   * ddl-auto=validate in every profile means the @Index annotations on
--     the entities are documentation only - they never reach the database.
--
-- All statements are idempotent (if not exists / if exists) so this script
-- is also safe when replayed against a database created from scratch.
-- =====================================================================


-- ---------------------------------------------------------------------
-- attendance
--   1. findByEmployeeAndAttendanceDate / existsByEmployeeAndAttendanceDate
--      / findByEmployeeAndAttendanceDateBetween
--      / countByEmployeeAndAttendanceDateBetweenAndAttendanceStatus
--      -> check-in, check-out, per-employee attendance views and the
--         per-employee payroll day count (runs for every employee).
--   2. findByAttendanceDateAndAttendanceStatusIn (notification job, every
--      5 min) and
--      findByAttendanceDateAndCheckInTimeIsNotNullAndCheckOutTimeIsNull[*]
--      (missed-checkout job, every 1 min). These filter by attendance_date
--      only, so the employee-leading composite cannot serve them.
-- ---------------------------------------------------------------------

create index if not exists idx_attendance_employee_date
    on attendance (employee_id, attendance_date);

create index if not exists idx_attendance_date
    on attendance (attendance_date);


-- ---------------------------------------------------------------------
-- attendance_breaks
--   findByAttendance (attendance detail) and
--   findFirstByAttendanceAndBreakEndTimeIsNullOrderByBreakStartTimeDesc
--   (break start/stop). Also the FK-check when an attendance row is deleted.
-- ---------------------------------------------------------------------

create index if not exists idx_attendance_breaks_attendance
    on attendance_breaks (attendance_id);


-- ---------------------------------------------------------------------
-- attendance_regularization
--   findByEmployeeOrderByCreatedAtDesc (my regularizations),
--   findPendingByManagerId / findAllByManagerId (manager inbox - the
--   employee join is already covered by idx_employees_reporting_manager).
-- ---------------------------------------------------------------------

create index if not exists idx_attendance_regularization_employee
    on attendance_regularization (employee_id);


-- ---------------------------------------------------------------------
-- attendance_regularization_detail
--   findByRegularizationId + countByRegularizationId[/AndStatus]
--   (approval flow, per detail) and findActiveByAttendanceId
--   (attendance detail view).
-- ---------------------------------------------------------------------

create index if not exists idx_attendance_regularization_detail_reg
    on attendance_regularization_detail (regularization_id);

create index if not exists idx_attendance_regularization_detail_attendance
    on attendance_regularization_detail (attendance_id);


-- ---------------------------------------------------------------------
-- employees
--   EmployeeSpecification.reportingManager (manager dashboards),
--   findByEmployeeReportingManager for leave requests and the manager
--   regularization queries.
-- ---------------------------------------------------------------------

create index if not exists idx_employees_reporting_manager
    on employees (reporting_manager_id);


-- ---------------------------------------------------------------------
-- designations / job_titles
--   LookupServiceImpl.getDesignations(departmentId) and
--   getJobTitles(designationId) - the columns are the WHERE predicate.
-- ---------------------------------------------------------------------

create index if not exists idx_designations_department
    on designations (department_id);

create index if not exists idx_job_titles_designation
    on job_titles (designation_id);


-- ---------------------------------------------------------------------
-- leave_requests
--   1. findByEmployee (leave history, calendar) and the overlap check
--      existsByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDate-
--      GreaterThanEqual (every leave application). The composite also
--      covers queries filtered by employee_id alone (leftmost prefix).
--   2. findByStatus-equivalent filtering: CalendarServiceImpl
--      findCalendarLeaves, LeaveReportSpecification (status + date range)
--      and the month-end aggregate sumApprovedLeaveDaysInMonthGrouped.
--   3. processLeaveNotifications runs every 5 minutes and reads only the
--      rows changed in the last 5 minutes (findByUpdatedAtBetween).
-- ---------------------------------------------------------------------

create index if not exists idx_leave_requests_employee_start
    on leave_requests (employee_id, start_date);

create index if not exists idx_leave_requests_status_start
    on leave_requests (status, start_date);

create index if not exists idx_leave_requests_updated_at
    on leave_requests (updated_at);


-- ---------------------------------------------------------------------
-- leave_transactions
--   findByEmployee / findByEmployeeId (per-employee ledger) and
--   findByLeaveRequest (transactions of one request).
-- ---------------------------------------------------------------------

create index if not exists idx_leave_transactions_employee
    on leave_transactions (employee_id);

create index if not exists idx_leave_transactions_leave_request
    on leave_transactions (leave_request_id);


-- ---------------------------------------------------------------------
-- leave_approvals
--   findByLeaveRequestOrderByCreatedAtAsc (approval trail of a request).
-- ---------------------------------------------------------------------

create index if not exists idx_leave_approvals_leave_request
    on leave_approvals (leave_request_id);


-- ---------------------------------------------------------------------
-- notifications
--   1. countByEmployeeAndIsReadFalse (unread badge, polled by the client)
--      and markAllAsRead.
--   2. findByEmployeeOrderByCreatedAtDesc (paged notification list).
-- ---------------------------------------------------------------------

create index if not exists idx_notifications_employee_read
    on notifications (employee_id, is_read);

create index if not exists idx_notifications_employee_created
    on notifications (employee_id, created_at);


-- ---------------------------------------------------------------------
-- performance_reviews
--   PerformanceReviewSpecification.hasReviewer (manager filtering the
--   reviews they wrote). employee_id is already the leftmost column of
--   uk_employee_review_period.
-- ---------------------------------------------------------------------

create index if not exists idx_performance_reviews_reviewer
    on performance_reviews (reviewer_id);


-- ---------------------------------------------------------------------
-- payrolls
--   existsByEmployeeIdAndPayrollMonthAndActiveTrue (payroll generation,
--   once per employee), existsByEmployeeIdAndPayrollMonthAndStatusNot,
--   findFirstByEmployeeIdAndPayrollMonth* and
--   findByEmployeeIdOrderByPayrollMonthDescVersionDesc (payroll history).
--   Replaces the single-column idx_payroll_employee, whose queries are all
--   a leftmost prefix of this composite.
-- ---------------------------------------------------------------------

create index if not exists idx_payrolls_employee_month
    on payrolls (employee_id, payroll_month);

drop index if exists idx_payroll_employee;


-- ---------------------------------------------------------------------
-- salary_structures
--   findByEmployeeIdAndStatus / existsByEmployeeIdAndStatus (active
--   structure of an employee, hit during payroll generation).
--   Replaces the single-column idx_salary_structure_employee, whose
--   queries are all a leftmost prefix of this composite.
-- ---------------------------------------------------------------------

create index if not exists idx_salary_structures_employee_status
    on salary_structures (employee_id, status);

drop index if exists idx_salary_structure_employee;


-- ---------------------------------------------------------------------
-- payroll_history
--   findByPayrollIdOrderByCreatedAtDesc (version history of a payroll).
-- ---------------------------------------------------------------------

create index if not exists idx_payroll_history_payroll
    on payroll_history (payroll_id);


-- ---------------------------------------------------------------------
-- users
--   Login: findByUsernameOrEmail / findByUsername / findByEmail and the
--   duplicate checks in AdminServiceImpl / AuthenticationServiceImpl.
--   Created as ordinary (non-unique) indexes on purpose: the uniqueness
--   of username/email is enforced in the service layer, and a UNIQUE index
--   would fail this migration on any legacy duplicate rows. Promote to
--   uk_* later once the data has been audited.
-- ---------------------------------------------------------------------

create index if not exists idx_users_username
    on users (username);

create index if not exists idx_users_email
    on users (email);


-- ---------------------------------------------------------------------
-- refresh_tokens
--   findByUserAndRevokedFalse (active sessions) and deleteByUser
--   (logout / user deletion).
-- ---------------------------------------------------------------------

create index if not exists idx_refresh_tokens_user_revoked
    on refresh_tokens (user_id, revoked);
