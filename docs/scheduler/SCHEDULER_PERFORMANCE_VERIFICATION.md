# Scheduler & Notification Performance — Verification Checklist

Applies to the performance pass over schedulers and transactional code:
multi-threaded task scheduler, bulk notification creation, SQL-side birthday/anniversary
filtering, narrowed missed-checkout query, and batched leave-expiry/allocation reads.

## 1. Pre-deploy

- [ ] Deploy/run against a **local or staging** database first. The `dev` profile points at
      a live Aiven PostgreSQL with `ddl-auto=update` — do not boot against it for verification.
- [ ] Confirm intended cron expressions. Comments in `NotificationScheduler` are misleading
      ("dev: every 5 seconds" vs "prod: every 5 minutes") while both lines are `0 */5 * * * *`.
      Verify the active cron matches what you want before deploying.
- [ ] `mvnw compile` passes.

## 2. Startup

- [ ] App starts with no HQL/query parsing errors (birthday/anniversary `MONTH()`/`DAY()`
      queries and the grouped leave-expiry query are validated at startup).
- [ ] Scheduler thread pool is active: thread dumps / logs show threads named `scheduled-1..4`.
- [ ] With `spring.jpa.show-sql=true` (dev profile), SQL output is visible for query-count checks.

## 3. What to watch in logs

- [ ] **Errors**: a failed scheduled task logs `Scheduled task failed` at ERROR
      (`SchedulingConfig` error handler) instead of failing silently.
- [ ] **No blocked jobs**: daily jobs and the 1-min/5-min jobs interleave instead of serializing.
      E.g. "Processing attendance notifications..." appears on time even while a birthday run is in progress.
- [ ] Existing markers still appear as before:
      - `Birthday notifications sent successfully for N employee(s).`
      - `Successfully sent work anniversary notifications to N employee(s).`
      - `Today's holiday notifications sent.` / `Tomorrow holiday reminder sent.`
      - `Leave expiry started/completed for month: ...` and `Expired N day(s) for employee ...`
      - `Allocated N days of ... for employee ... for year ...`

## 4. Functional spot-checks (behavior unchanged)

- [ ] **Birthday / anniversary**: on a day with matches, the person gets the MEDIUM "wish"
      notification and every other active employee gets the LOW "celebration" one. Run the
      scheduler again — second run inserts nothing (dedup still works).
- [ ] **Holiday**: holiday day + reminder notifications created once per employee; re-running
      adds no duplicates.
- [ ] **Announcement broadcast**: every employee receives exactly one ANNOUNCEMENT
      notification; posting the same announcement again does not duplicate per employee.
- [ ] **Attendance notifications**: a late check-in creates LATE_CHECK_IN for the employee and
      a GENERAL notification for the reporting manager, once. The 5-min processor re-runs all
      day without creating duplicates.
- [ ] **Missed checkout**: after office-end + cutoff, checked-in/not-checked-out rows flip to
      `MISSED_CHECKOUT` once; subsequent 1-min runs do zero updates (SQL log shows the narrowed
      query returning 0 rows).
- [ ] **Leave expiry (month-end)**: `expiredLeaves`/`remainingLeaves` math is identical to the
      old algorithm (compare against a staging copy of data). Only the reads are batched.
- [ ] **Leave allocation (Jan 1 / /all-employee)**: one balance per active employee × active
      leave type; re-running adjusts existing balances only when `allocatedDays` changed.

## 5. Performance indicators

- [ ] Birthday run = ~2 queries per birthday person (1 batch exists-check + 1 batch insert)
      instead of `2 × (employees + 1)`.
- [ ] Holiday / announcement broadcast = 2 queries total, not `2 × employees`.
- [ ] 5-min notification processor = a handful of queries (status-filtered fetch + grouped
      exists-checks + batch inserts), not `2 × rows`.
- [ ] Month-end expiry = 2 read queries (balances by year, used-days grouped) + writes, instead
      of `2 × employees × leaveTypes` reads.

## 6. Caveats & rollback

- [ ] Jobs now run in parallel; a job that takes longer than its own interval could overlap
      itself. After these changes each run is milliseconds, but if you ever see duplicate
      notifications from one job, that is the cause — the dedup check is not race-proof.
- [ ] Rollback: revert the scheduler/notification/leave service changes in this pass. The
      changes are isolated to those files; no schema or API changes were made.
