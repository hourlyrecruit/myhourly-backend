-- =====================================================================
-- V9 - PAID / LOP classification and approver on leave requests
-- =====================================================================
-- The monthly paid-leave guideline (leave_settings.monthly_guideline) caps how
-- many approved leave days in a calendar month are PAID. Days beyond that cap,
-- or beyond the employee's available annual balance, are LOP (Loss of Pay) and
-- must NOT be deducted from the annual balance.
--
-- A single request can therefore contain both kinds of day, so the split is
-- stored per request instead of a single PAID/LOP flag:
--
--   paid_days  - days deducted from the annual leave balance
--   lop_days   - days NOT deducted (over the monthly allowance / balance)
--
-- Historical rows are deliberately left NULL. This migration does not rewrite
-- the past: before this column existed every approved day was simply deducted,
-- so a NULL paid_days is read as "legacy, treat as fully deducted" by
-- LeaveRequestRepository#sumPaidLeaveDaysInMonth (COALESCE with total_days),
-- while a non-NULL value is authoritative. That keeps month-end expiry and
-- monthly-allowance accounting correct for pre-existing approvals without
-- inventing an LOP split the old system never had.
--
-- approved_by records the authenticated approver on the request itself. The
-- leave_approvals table remains the full audit trail (one row per action); this
-- column is the fast, denormalised pointer the API needs for list views.
--
-- All statements are idempotent so this script is safe to replay against a
-- database created from scratch.
-- =====================================================================

alter table if exists leave_requests
    add column if not exists paid_days integer;

alter table if exists leave_requests
    add column if not exists lop_days integer;

alter table if exists leave_requests
    add column if not exists approved_by bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_leave_requests_approved_by'
    ) then
        alter table leave_requests
            add constraint fk_leave_requests_approved_by
            foreign key (approved_by)
            references employees;
    end if;
end
$$;

-- PostgreSQL does not index the referencing side of a foreign key, and the
-- approver is read for every approved request in the list/detail views.
create index if not exists idx_leave_requests_approved_by
    on leave_requests (approved_by);
