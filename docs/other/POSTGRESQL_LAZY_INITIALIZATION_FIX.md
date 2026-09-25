# PostgreSQL LazyInitializationException Fix

## Problem
The report module was throwing `LazyInitializationException` in PostgreSQL but working fine in MySQL:

```
org.hibernate.LazyInitializationException: Could not initialize proxy [com.my_hourly.employee.entity.Employee#2] - no session
at com.my_hourly.report.service.impl.AttendanceReportServiceImpl.mapToResponse(AttendanceReportServiceImpl.java:119)
```

## Root Cause
The issue occurs because:

1. **Lazy Loading**: The `Employee` and `Department` relationships in `Attendance` and `LeaveRequest` entities are marked with `@ManyToOne(fetch = FetchType.LAZY)`

2. **Session Closure**: When using `repository.findAll(specification)`, Hibernate closes the session after fetching the main entities

3. **Lazy Access Outside Session**: The mapper methods (`mapToResponse`, `mapToLeaveResponse`) try to access lazy-loaded properties like `employee.getEmployeeCode()`, `employee.getFirstName()`, `department.getDepartmentName()` AFTER the session is closed

4. **Database Differences**: PostgreSQL is stricter about session management and proxy initialization compared to MySQL

## Solution Applied

We fixed this with a **two-pronged approach**:

### 1. Added `@Transactional(readOnly = true)` to Service Classes

**AttendanceReportServiceImpl.java:**
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // ← Added this
public class AttendanceReportServiceImpl implements AttendanceReportService {
    // ... methods
}
```

**LeaveReportServiceImpl.java:**
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // ← Added this
public class LeaveReportServiceImpl implements LeaveReportService {
    // ... methods
}
```

**Why this helps:**
- Keeps the Hibernate session open for the entire duration of the service method
- Allows lazy-loaded associations to be fetched on-demand
- `readOnly = true` optimizes performance by telling Hibernate not to track changes

### 2. Added JOIN FETCH in Specifications

**AttendanceReportSpecification.java:**
```java
public static Specification<Attendance> filter(AttendanceReportRequest request) {
    return (root, query, cb) -> {
        
        // Add FETCH joins to eagerly load associations (only for non-count queries)
        if (query != null && Long.class != query.getResultType()) {
            root.fetch("employee", JoinType.LEFT);
            root.fetch("employee").fetch("department", JoinType.LEFT);
        }
        
        // ... rest of predicates
    };
}
```

**LeaveReportSpecification.java:**
```java
public static Specification<LeaveRequest> filter(LeaveReportRequest request) {
    return (root, query, cb) -> {
        
        // Add FETCH joins to eagerly load associations (only for non-count queries)
        if (query != null && Long.class != query.getResultType()) {
            root.fetch("employee", JoinType.LEFT);
            root.fetch("employee").fetch("department", JoinType.LEFT);
            root.fetch("leaveType", JoinType.LEFT);
        }
        
        // ... rest of predicates
    };
}
```

**Why this helps:**
- **Eager Loading**: JOIN FETCH tells Hibernate to load the associated entities in the same query
- **Performance**: Reduces N+1 query problem by fetching all data in one SQL query
- **Count Query Check**: The `Long.class != query.getResultType()` check prevents fetch joins in count queries (which would cause errors)

### 3. Why Check for Count Queries?

```java
if (query != null && Long.class != query.getResultType()) {
    // Only add fetch joins for data queries, not count queries
}
```

Spring Data JPA executes **two queries** when using `Page`:
1. **Data query**: `SELECT * FROM ... JOIN ...` (needs fetch joins)
2. **Count query**: `SELECT COUNT(*) FROM ...` (fetch joins would cause error)

The check ensures fetch joins are only applied to data queries.

## Benefits of This Approach

✅ **Works with PostgreSQL**: No more LazyInitializationException  
✅ **Performance Improvement**: Reduces N+1 queries (1 query instead of N+1)  
✅ **Thread-Safe**: `@Transactional` manages transactions properly  
✅ **Read-Only Optimization**: PostgreSQL can optimize read-only transactions  
✅ **No Entity Changes**: Didn't need to modify entity fetch types  
✅ **Module Isolation**: Only changed report module as requested

## SQL Query Comparison

### Before (Lazy Loading - N+1 Problem):
```sql
-- Query 1: Get attendances
SELECT * FROM attendances WHERE ...;

-- Query 2: Get employee for attendance 1
SELECT * FROM employees WHERE id = 1;

-- Query 3: Get department for employee 1  
SELECT * FROM departments WHERE id = 5;

-- Query 4: Get employee for attendance 2
SELECT * FROM employees WHERE id = 2;

-- ... N more queries for each record
```

### After (Eager Loading with JOIN FETCH):
```sql
-- Single query with joins
SELECT a.*, e.*, d.*
FROM attendances a
LEFT JOIN employees e ON a.employee_id = e.id
LEFT JOIN departments d ON e.department_id = d.id
WHERE ...;
```

## Testing

Test with both databases to ensure compatibility:

```bash
# Test with PostgreSQL
GET /api/v1/reports/attendance?month=7&year=2026

# Test with MySQL
GET /api/v1/reports/attendance?month=7&year=2026
```

Both should now work without LazyInitializationException.

## Files Modified

1. ✅ `AttendanceReportServiceImpl.java` - Added `@Transactional(readOnly = true)`
2. ✅ `LeaveReportServiceImpl.java` - Added `@Transactional(readOnly = true)`
3. ✅ `AttendanceReportSpecification.java` - Added JOIN FETCH for employee and department
4. ✅ `LeaveReportSpecification.java` - Added JOIN FETCH for employee, department, and leaveType

## Alternative Solutions (Not Used)

We chose not to use these approaches:

1. **Entity-level eager loading** - Would affect all queries, not just reports
2. **`@EntityGraph`** - More complex and less flexible than specifications
3. **Manual Hibernate.initialize()** - Less efficient than JOIN FETCH
4. **DTOs in repository** - Requires changing repository layer

## Summary

The fix ensures that all necessary data is loaded while the Hibernate session is active, preventing lazy initialization exceptions in PostgreSQL while also improving performance through reduced database roundtrips.
